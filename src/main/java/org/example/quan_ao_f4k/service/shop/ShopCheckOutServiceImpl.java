package org.example.quan_ao_f4k.service.shop;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.quan_ao_f4k.dto.response.shop.ShopProductResponse;
import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.mapper.shop.ShopProductMapper;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.model.order.*;
import org.example.quan_ao_f4k.model.promotion.Promotion;
import org.example.quan_ao_f4k.repository.order.*;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.example.quan_ao_f4k.util.F4KUtils;
import org.example.quan_ao_f4k.util.HoaDonUtils;
import org.example.quan_ao_f4k.util.JacksonEx;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ShopCheckOutServiceImpl implements ShopCheckOutService {
    private final F4KUtils f4KUtils;
    private final ShopCartService shopCartService;
    private final ShopProductService shopProductService;

    private final ShippingInfoRepository shippingInfoRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;

    private final ShopProductMapper shopProductMapper;
    private final OrderHistoryRepository orderHistoryRepository;

    @Override
    public void addModelCheckout(Model model) {
        User user = f4KUtils.getUser();
        Cart cart = shopCartService.getCart(user.getId());

        ShopProductResponse.CartResponse cartResponse = shopCartService.getListCart(user.getUsername());

        List<ShippingInfo> shippingInfo = shippingInfoRepository.findAllByUserId(user.getId());

        model.addAttribute("cartId", cart.getId());
        model.addAttribute("carts", cartResponse);
        model.addAttribute("shippingInfoList", shippingInfo);
    }

    @Override
    @Transactional
    public Order createOneOrder(HoaDonUtils.PhuongThucMuaHang phuongThucMuaHang, String note, boolean isClear) {
        User user = f4KUtils.getUser();

        Order newOrder = initOrder(user, phuongThucMuaHang, note);
        Order savedOrder = orderRepository.save(newOrder);

        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setOrder(savedOrder);
        orderHistory.setCreatedAt(LocalDateTime.now());
        if (note == null || note.isEmpty()) {
            orderHistory.setNote("Đơn hàng đã được đặt, chờ phê duyệt");
        } else {
            orderHistory.setNote("Đơn hàng đã được đặt, chờ phê duyệt <br/> ghi chú: " + note);
        }
        orderHistory.setCreateBy("Người dùng");
        orderHistory.setStatus(savedOrder.getStatus());
        orderHistoryRepository.save(orderHistory);

        Cart cart = getUserCart(user);
        List<CartProduct> cartProducts = getCartProducts(cart);

        List<OrderDetail> orderDetails = convertCartProductsToOrderDetails(cartProducts, savedOrder);
        orderDetailRepository.saveAll(orderDetails);

        if (isClear) {
            clearCart(user);
        }
        return savedOrder;
    }

    @Override
    public void cancelOrder(Long orderId, String note) {
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new BadRequestException("Hoá đơn không tồn tại")
        );

        if (order.getStatus() != HoaDonUtils.TrangThaiHoaDon.CHO_XAC_NHAN.getStatus()) {
            throw new BadRequestException(String.format("Hoá đơn đang ở trạng thái %s không thể thao tác"
                    , HoaDonUtils.TrangThaiHoaDon.getMessByStatus(order.getStatus())));
        } else {
            order.setStatus(HoaDonUtils.TrangThaiHoaDon.HUY_DON.getStatus());
            order.setNote(note);
            orderRepository.save(order);
        }
    }

    @Override
    public List<OrderDetail> getOrderDetailByOrder(Order order) {
        return orderDetailRepository.findAllByOrder_Id(order.getId());
    }

    @Override
    public void addModalPurchaseHistory(Model model) {
        User user = f4KUtils.getUser();
        model.addAttribute("user", user);

        model.addAttribute("status_all", getOrderResponse(user.getId(), null));
        model.addAttribute("status_wait_confirm", getOrderResponse(user.getId(), HoaDonUtils.TrangThaiHoaDon.CHO_XAC_NHAN.getStatus()));
        model.addAttribute("status_wait_delivery", getOrderResponse(user.getId(), HoaDonUtils.TrangThaiHoaDon.CHO_LAY_HANG.getStatus()));
        model.addAttribute("status_on_delivery", getOrderResponse(user.getId(), HoaDonUtils.TrangThaiHoaDon.DANG_GIAO_HANG.getStatus()));
        model.addAttribute("status_complete", getOrderResponse(user.getId(), HoaDonUtils.TrangThaiHoaDon.HOAN_TAT.getStatus()));
        model.addAttribute("status_cancel", getOrderResponse(user.getId(), HoaDonUtils.TrangThaiHoaDon.HUY_DON.getStatus()));
    }

    @Override
    public void addModalPurchaseHistoryDetail(Model model, String code) {
        User user = f4KUtils.getUser();
        model.addAttribute("user", user);

        ShopProductResponse.OrderResponse orderResponse = getOderDetailResponse(code, user, model);
        model.addAttribute("orderDetail", orderResponse);

        List<OrderHistory> listOrderHistory = orderHistoryRepository.findByOrderCode(code);
        model.addAttribute("history", listOrderHistory);
    }

    private ShopProductResponse.OrderResponse getOderDetailResponse(String code, User user, Model model) {
        Order order = orderRepository.findByCodeAAndUser_Id(code, user.getId()).orElse(null);
        if (order == null) {
            throw new NoSuchElementException("Không tồn tại đơn hàng: " + code);
        }
        ShopProductResponse.OrderDto orderDto = shopProductMapper.toOrderDto(order);

        List<OrderDetail> listOrderDetail = orderDetailRepository.findAllByOrderIdAndUserId(user.getId(), orderDto.getId());
        List<ShopProductResponse.OrderDetailDto> listOrderDetailDto = getListOrderDetail(listOrderDetail);

        if (orderDto.getTotalPay() != null) {
//            BigDecimal totalCart = orderDto.getTotalPay().subtract(orderDto.getShippingPay());
            BigDecimal totalCart = orderDto.getTotalPay();
            orderDto.setTotalCart(totalCart);
            if (orderDto.getShippingPay() != null) {
                orderDto.setInvoiceTotal(totalCart.add(orderDto.getShippingPay()));
            }
        }


        return ShopProductResponse.OrderResponse.builder()
                .orderDto(orderDto)
                .detailDtoList(listOrderDetailDto)
                .build();
    }

    private List<ShopProductResponse.OrderResponse> getOrderResponse(Long userId, Integer status) {
        List<Order> listOrder = orderRepository.findOrdersByStatusAndType(HoaDonUtils.LoaiHoaDon.ONLINE, status);
        List<ShopProductResponse.OrderDto> listOrderDto = shopProductMapper.toOrderDto(listOrder);

        List<ShopProductResponse.OrderResponse> listOrderResponse = new ArrayList<>();

        for (ShopProductResponse.OrderDto orderDto: listOrderDto) {
            List<OrderDetail> listOrderDetail = orderDetailRepository.findAllByOrderIdAndUserId(userId, orderDto.getId());
            List<ShopProductResponse.OrderDetailDto> listOrderDetailDto = getListOrderDetail(listOrderDetail);

            if (orderDto.getTotalPay() != null) {
//            BigDecimal totalCart = orderDto.getTotalPay().subtract(orderDto.getShippingPay());
                BigDecimal totalCart = orderDto.getTotalPay();
                orderDto.setTotalCart(totalCart);
                if (orderDto.getShippingPay() != null) {
                    orderDto.setInvoiceTotal(totalCart.add(orderDto.getShippingPay()));
                }
            }

            ShopProductResponse.OrderResponse orderResponse = ShopProductResponse.OrderResponse.builder()
                    .orderDto(orderDto)
                    .detailDtoList(listOrderDetailDto)
                    .build();
            listOrderResponse.add(orderResponse);
        }
        return listOrderResponse;
    }

    private List<ShopProductResponse.OrderDetailDto> getListOrderDetail(List<OrderDetail> orderDetails) {
        List<ShopProductResponse.OrderDetailDto> lstDto = shopProductMapper.toOrderDetailDto(orderDetails);
        for (ShopProductResponse.OrderDetailDto orderDetailDto: lstDto) {
            BigDecimal currentPrice = orderDetailDto.getProductDetailDto().getPrice();
            BigDecimal purchasePrice = orderDetailDto.getDiscountPrice();
            if (purchasePrice != null && purchasePrice.compareTo(currentPrice) < 0) {
                orderDetailDto.setPurchasePrice(purchasePrice);
            }
        }
        return lstDto;
    }

    @Override
    @Transactional
    public void clearCart(User user) {
        cartProductRepository.deleteAllByUser_Id(user.getId());
        cartRepository.deleteAllByUser_Id(user.getId());
    }

    private List<OrderDetail> convertCartProductsToOrderDetails(List<CartProduct> cartProducts, Order savedOrder) {
        return cartProducts.stream()
                .filter(cartProduct -> {
                    if (cartProduct.getProductDetail().getProduct().getStatus() == F4KConstants.STATUS_ON
                            && cartProduct.getProductDetail().getQuantity() > 0) {
                        return true;
                    } else {
                        return false;
                    }
                })
                .map(cartProduct -> {
                    OrderProductDetailKey key = createOrderProductDetailKey(savedOrder, cartProduct);
                    return createOrderDetail(cartProduct, savedOrder, key);
                })
                .collect(Collectors.toList());
    }

    private OrderDetail createOrderDetail(CartProduct cartProduct, Order savedOrder, OrderProductDetailKey key) {
        OrderDetail orderDetail = new OrderDetail();

        Promotion promotion = shopProductService.getBestPromotionForProductDetail(cartProduct.getProductDetail().getId());

        BigDecimal price = cartProduct.getProductDetail().getPrice();
        Integer quantity = cartProduct.getQuantity();

        if (promotion != null) {
            BigDecimal discountedPrice = shopProductService.calculateDiscountedPrice(price, promotion.getDiscountValue());
            orderDetail.setDiscountPrice(discountedPrice);
        }

//        BigDecimal finaPrice = price.multiply(BigDecimal.valueOf(quantity));

        orderDetail.setOrder(savedOrder);
        orderDetail.setOrderProductDetailKey(key);
        orderDetail.setProductDetail(cartProduct.getProductDetail());
        orderDetail.setQuantity(quantity);
        orderDetail.setPrice(price);
        return orderDetail;
    }

    private OrderProductDetailKey createOrderProductDetailKey(Order savedOrder, CartProduct cartProduct) {
        OrderProductDetailKey key = new OrderProductDetailKey();
        key.setOrderId(savedOrder.getId());
        key.setProductDetailId(cartProduct.getProductDetail().getId());
        return key;
    }

    private List<CartProduct> getCartProducts(Cart cart) {
        return cartProductRepository.findAllByCart_Id(cart.getId());
    }

    private Cart getUserCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    log.error("Không tìm thấy cart với user id = {}", user.getId());
                    return new BadRequestException("Không tìm thấy cart với user id = " + user.getId());
                });
    }

    private Order initOrder(User user, HoaDonUtils.PhuongThucMuaHang phuongThucMuaHang, String note) {
        ShippingInfo shippingInfo = getDefaultShippingInfo(user);
//        BigDecimal subtotal = getSubtotal(user);
        BigDecimal totalShippingCost = calculateShippingCost(shippingInfo);
//        BigDecimal cartTotal = calculateCartTotal(user);
//        BigDecimal subtotal = totalShippingCost.add(cartTotal);
        BigDecimal subtotal = calculateCartTotal(user);

        Order order = new Order();
        order.setCode(HoaDonUtils.taoMaHoaDon());
        order.setOrder_type(HoaDonUtils.LoaiHoaDon.ONLINE);
        order.setCreatedAt(LocalDateTime.now());
        order.setUser(user);
        order.setStatus(HoaDonUtils.TrangThaiHoaDon.CHO_XAC_NHAN.getStatus());
        order.setPaymentStatus(HoaDonUtils.TrangThaiThanhToan.CHUA_THANH_TOAN);

        order.setToName(shippingInfo.getRecipientName());
        order.setToAddress(String.format("%s, %s, %s, %s"
                , shippingInfo.getAddressDetail()
                , shippingInfo.getWardName()
                , shippingInfo.getDistrictName()
                , shippingInfo.getProvinceName()));
        order.setToPhone(shippingInfo.getPhoneNumber());
        order.setTotalPay(subtotal);
        order.setShippingPay(totalShippingCost);

        switch (phuongThucMuaHang) {
            case CHUYEN_TIEN -> {
                order.setPaymentMethod(paymentMethodRepository.findById(2L).get());
                order.setNote(HoaDonUtils.ORDER_NOTE_ONLINE);
            }
            case THANH_TOAN_SAU_NHAN_HANG -> {
                order.setPaymentMethod(paymentMethodRepository.findById(1L).get());
                order.setNote(HoaDonUtils.ORDER_NOTE);
            }
        }

        if (note != null && !note.isEmpty()) {
            order.setNote(order.getNote() + "<br/> ghi chú: " + note);
        }
        return order;
    }

    private ShippingInfo getDefaultShippingInfo(User user) {
        ShippingInfo shippingInfo = shippingInfoRepository.findDefaultByUserId(user.getId());
        if (shippingInfo == null) {
            throw new BadRequestException("Không tìm thấy thông tin vận chuyển với user = " + user.getUsername());
        }
        return shippingInfo;
    }

    private BigDecimal calculateShippingCost(ShippingInfo shippingInfo) {
        String free = shopCartService.getFree(Math.toIntExact(shippingInfo.getDistrictId()), shippingInfo.getWardCode());
        JsonNode freeNode = JacksonEx.readFormText(free).get("data");

        if (freeNode == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalShip = JacksonEx.getDataFromJsonNode(freeNode, "total", BigDecimal.class);
        return totalShip != null ? totalShip : BigDecimal.ZERO;
    }

    private BigDecimal calculateCartTotal(User user) {
        ShopProductResponse.CartResponse cartResponse = shopCartService.getListCart(user.getUsername());
        return cartResponse.getItems().stream()
                .map(ShopProductResponse.CartProductDto::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
