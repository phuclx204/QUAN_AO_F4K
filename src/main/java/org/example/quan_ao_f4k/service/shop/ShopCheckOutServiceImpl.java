package org.example.quan_ao_f4k.service.shop;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.quan_ao_f4k.dto.response.shop.ShopProductResponse;
import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.model.order.*;
import org.example.quan_ao_f4k.repository.order.*;
import org.example.quan_ao_f4k.util.F4KUtils;
import org.example.quan_ao_f4k.util.HoaDonUtils;
import org.example.quan_ao_f4k.util.JacksonEx;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ShopCheckOutServiceImpl implements ShopCheckOutService {
    private final F4KUtils f4KUtils;
    private final ShopCartService shopCartService;

    private final ShippingInfoRepository shippingInfoRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;

    @Override
    public void addModelCheckout(Model model) {
        User user = f4KUtils.getUser();
        Cart cart = shopCartService.getCart(user.getId());
        model.addAttribute("cartId", cart.getId());
        ShopProductResponse.CartResponse cartResponse = shopCartService.getListCart(user.getUsername());
        model.addAttribute("carts", cartResponse);
    }


    @Override
    @Transactional
    public Order createOrder(HoaDonUtils.PhuongThucMuaHang phuongThucMuaHang) {
        return createOrder(phuongThucMuaHang, true);
    }

    @Override
    @Transactional
    public Order createOrder(HoaDonUtils.PhuongThucMuaHang phuongThucMuaHang, boolean isClear) {
        User user = f4KUtils.getUser();

        Order newOrder = initOrder(user, phuongThucMuaHang);
        Order savedOrder = orderRepository.save(newOrder);
        Cart cart = getUserCart(user);
        List<CartProduct> cartProducts = getCartProducts(cart);

        List<OrderDetail> orderDetails = convertCartProductsToOrderDetails(cartProducts, savedOrder);
        saveOrderDetails(orderDetails);

        if (isClear) clearCart(user);
        return savedOrder;
    }

    @Override
    public List<OrderDetail> getOrderDetailByOrder(Order order) {
        return orderDetailRepository.findAllByOrder_Id(order.getId());
    }

    @Override
    public void addModalPurchaseHistory(Model model) {
        User user = f4KUtils.getUser();
        model.addAttribute("user", user);

        List<OrderDetail> status_all = orderDetailRepository.findAllByOrderUserIdAndOrderStatus(user.getId(), null);
        List<OrderDetail> status_wait_confirm = orderDetailRepository.findAllByOrderUserIdAndOrderStatus(user.getId(), HoaDonUtils.TrangThaiHoaDon.CHO_XAC_NHAN.getStatus());
        List<OrderDetail> status_wait_delivery = orderDetailRepository.findAllByOrderUserIdAndOrderStatus(user.getId(), HoaDonUtils.TrangThaiHoaDon.CHO_GIAO_HANG.getStatus());
        List<OrderDetail> status_on_delivery = orderDetailRepository.findAllByOrderUserIdAndOrderStatus(user.getId(), HoaDonUtils.TrangThaiHoaDon.DANG_GIAO_HANG.getStatus());
        List<OrderDetail> status_complete = orderDetailRepository.findAllByOrderUserIdAndOrderStatus(user.getId(), HoaDonUtils.TrangThaiHoaDon.HOAN_TAT.getStatus());
        List<OrderDetail> status_cancel = orderDetailRepository.findAllByOrderUserIdAndOrderStatus(user.getId(), HoaDonUtils.TrangThaiHoaDon.HUY_DON.getStatus());

        model.addAttribute("status_all", status_all);
        model.addAttribute("status_wait_confirm", status_wait_confirm);
        model.addAttribute("status_wait_delivery", status_wait_delivery);
        model.addAttribute("status_on_delivery", status_on_delivery);
        model.addAttribute("status_complete", status_complete);
        model.addAttribute("status_cancel", status_cancel);
    }

    @Override
    @Transactional
    public void clearCart(User user) {
        cartProductRepository.deleteAllByUser_Id(user.getId());
        cartRepository.deleteAllByUser_Id(user.getId());
    }

    private List<OrderDetail> convertCartProductsToOrderDetails(List<CartProduct> cartProducts, Order savedOrder) {
        return cartProducts.stream()
                .map(cartProduct -> {
                    OrderProductDetailKey key = createOrderProductDetailKey(savedOrder, cartProduct);
                    return createOrderDetail(cartProduct, savedOrder, key);
                })
                .collect(Collectors.toList());
    }

    private OrderDetail createOrderDetail(CartProduct cartProduct, Order savedOrder, OrderProductDetailKey key) {
        return OrderDetail.builder()
                .order(savedOrder)
                .productDetail(cartProduct.getProductDetail())
                .quantity(cartProduct.getQuantity())
                .price(cartProduct.getProductDetail().getPrice()
                        .multiply(BigDecimal.valueOf(cartProduct.getQuantity())))
                .orderProductDetailKey(key)
                .build();
    }

    private void saveOrderDetails(List<OrderDetail> orderDetails) {
        orderDetailRepository.saveAll(orderDetails);
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

    private Order initOrder(User user, HoaDonUtils.PhuongThucMuaHang phuongThucMuaHang) {
        ShippingInfo shippingInfo = getDefaultShippingInfo(user);
        BigDecimal subtotal = getSubtotal(user);

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

        switch (phuongThucMuaHang) {
            case CHUYEN_TIEN -> {
                order.setPaymentMethod(paymentMethodRepository.findById(2L).get());
                order.setNote(HoaDonUtils.ORDER_NOTE_ONLINE);
            }
            case THANH_TOAN_SAU_NHAN_HANG -> {
                order.setPaymentMethod(paymentMethodRepository.findById(3L).get());
                order.setNote(HoaDonUtils.ORDER_NOTE);
            }
        }
        return order;
    }

    private BigDecimal getSubtotal(User user) {
        ShippingInfo shippingInfo = getDefaultShippingInfo(user);
        BigDecimal totalShippingCost = calculateShippingCost(shippingInfo);
        BigDecimal cartTotal = calculateCartTotal(user);

        return totalShippingCost.add(cartTotal);
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
