package org.example.quan_ao_f4k.service.order;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.order.OrderDetailRequest;
import org.example.quan_ao_f4k.dto.request.order.OrderDetailResponse;
import org.example.quan_ao_f4k.dto.response.orders.OrderHistoryResponse;
import org.example.quan_ao_f4k.dto.response.orders.OrderResponse;
import org.example.quan_ao_f4k.dto.response.product.ProductDetailResponse;
import org.example.quan_ao_f4k.dto.response.shop.ShopProductResponse;
import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.order.OrderDetailMapper;
import org.example.quan_ao_f4k.mapper.order.OrderHistoryMapper;
import org.example.quan_ao_f4k.mapper.order.OrderMapper;
import org.example.quan_ao_f4k.mapper.product.ProductDetailMapper;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.model.order.Order;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.model.order.OrderHistory;
import org.example.quan_ao_f4k.model.order.OrderProductDetailKey;
import org.example.quan_ao_f4k.model.product.*;
import org.example.quan_ao_f4k.model.promotion.Promotion;
import org.example.quan_ao_f4k.repository.order.OrderDetailRepository;
import org.example.quan_ao_f4k.repository.order.OrderHistoryRepository;
import org.example.quan_ao_f4k.repository.order.OrderRepository;
import org.example.quan_ao_f4k.repository.product.*;
import org.example.quan_ao_f4k.service.pomotion.PromotionService;
import org.example.quan_ao_f4k.service.product.ProductDetailService;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.example.quan_ao_f4k.util.F4KUtils;
import org.example.quan_ao_f4k.util.HoaDonUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class OrderDetailServiceimpl implements OrderDetailService {
    private final F4KUtils f4KUtils;

    private final ProductDetailService productDetailService;
    private final OrderHistoryService orderHistoryService;
    private final PromotionService promotionService;

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final ProductDetailRepository productDetailRepository;

    private final ProductDetailMapper productDetailMapper;
    private final OrderHistoryMapper orderHistoryMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final OrderMapper orderMapper;
    private final BrandRepository brandRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ListResponse<OrderDetailResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        return null;
    }

    @Override
    public OrderDetailResponse findById(OrderProductDetailKey orderProductDetailKey) {
        return null;
    }

    @Override
    public OrderDetailResponse save(OrderDetailRequest request) {
        OrderDetail orderDetail = orderDetailMapper.requestToEntity(request);

        OrderProductDetailKey pk = new OrderProductDetailKey();
        pk.setOrderId(orderDetail.getOrder().getId());
        pk.setProductDetailId(orderDetail.getProductDetail().getId());
        orderDetail.setOrderProductDetailKey(pk);
        orderDetail.setPrice(orderDetail.getProductDetail().getPrice());

        Promotion promotion = promotionService.getBestPromotionForProductDetail(orderDetail.getProductDetail().getId());
        if (promotion != null) {
            BigDecimal finalPrice = promotionService.calculateDiscountedPrice(orderDetail.getProductDetail().getPrice(), promotion.getDiscountValue());
            if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
                orderDetail.setDiscountPrice(null);
            } else {
                orderDetail.setDiscountPrice(finalPrice);
            }
        } else {
            orderDetail.setDiscountPrice(null);
        }

        OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);
        orderHistoryService.insertOrderHistory(orderDetail.getOrder(), orderDetail.getOrder().getStatus(), "Thêm sản phẩm", f4KUtils.getUser());
        return orderDetailMapper.entityToResponse(savedOrderDetail);
    }

    @Override
    public OrderDetailResponse save(OrderProductDetailKey orderProductDetailKey, OrderDetailRequest request) {
        return defaultSave(orderProductDetailKey, request, orderDetailRepository, orderDetailMapper, "");
    }

    @Override
    public void delete(OrderProductDetailKey orderProductDetailKey) {
        orderDetailRepository.deleteById(orderProductDetailKey);

    }

    @Override
    public void delete(List<OrderProductDetailKey> orderProductDetailKeys) {
        orderDetailRepository.deleteAllById(orderProductDetailKeys);
    }
    @Override
    public void updateQuantity(Long productId, int quantity) {
        // Tìm thông tin chi tiết của sản phẩm theo productId
        Optional<ProductDetail> optionalProductDetail = productDetailRepository.findById(productId);

        if (optionalProductDetail.isPresent()) {
            ProductDetail productDetail = optionalProductDetail.get();

            int currentQuantity = productDetail.getQuantity();

            int updatedQuantity = currentQuantity - quantity;

            productDetail.setQuantity(updatedQuantity);

            productDetailRepository.save(productDetail);
        } else {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId);
        }
    }

    @Override
    public void updateQuantityPlus(Long productId, int quantity) {
        // Tìm thông tin chi tiết của sản phẩm theo productId
        Optional<ProductDetail> optionalProductDetail = productDetailRepository.findById(productId);

        if (optionalProductDetail.isPresent()) {
            ProductDetail productDetail = optionalProductDetail.get();

            int currentQuantity = productDetail.getQuantity();

            int updatedQuantity = currentQuantity + quantity;

            productDetail.setQuantity(updatedQuantity);

            productDetailRepository.save(productDetail);
        } else {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId);
        }
    }
    @Override
    public List<OrderDetail> getProductDetailsByOrderId(Long orderId) {
        return orderDetailRepository.findProductDetailsByOrderId(orderId);
    }

    /** Cấm khứa nào đụng - sonng **/
    @Override
    public void addModelOrderDetail(Model model, String code) {
        Order order = orderRepository.findOrderByOrderCode(code);
        OrderResponse orderResponse = orderMapper.entityToResponse(order);
        orderResponse.setStatusText(HoaDonUtils.TrangThaiHoaDon.getMessByStatus(orderResponse.getStatus()));

        orderResponse.setTotalCart(orderResponse.getTotalPay());
        if (orderResponse.getShippingPay() != null) {
            orderResponse.setTotalPay(orderResponse.getShippingPay().add(orderResponse.getTotalPay()));
        }

        List<OrderHistory> orderHistories = orderHistoryRepository.findByOrderId(order.getId());
        List<OrderHistoryResponse> listOrderHistoryResponse = orderHistoryMapper.entityToResponse(orderHistories);

        List<OrderDetail> listOrderDetail = orderDetailRepository.findAllByOrderIdAndUserId(null, order.getId());
        List<OrderDetailResponse> listOrderDetailResponse = getListOrderDetail(listOrderDetail);

        List<Brand> listBrand = brandRepository.findByStatus(F4KConstants.STATUS_ON);
        List<Color> listColor = colorRepository.findByStatus(F4KConstants.STATUS_ON);
        List<Size> listSize = sizeRepository.findByStatus(F4KConstants.STATUS_ON);
        List<Category> listCategory = categoryRepository.findByStatus(F4KConstants.STATUS_ON);

        model.addAttribute("listBrand", listBrand);
        model.addAttribute("listColor", listColor);
        model.addAttribute("listSize", listSize);
        model.addAttribute("listCategory", listCategory);

        model.addAttribute("orderDetails", listOrderDetailResponse);
        model.addAttribute("orderHistory", listOrderHistoryResponse);
        model.addAttribute("order", orderResponse);
        model.addAttribute("hasPromotion", checkOrderHasPromotion(listOrderDetail));
        model.addAttribute("isOnline", orderResponse.getOrder_type().equals(HoaDonUtils.LoaiHoaDon.ONLINE));
    }
    private List<OrderDetailResponse> getListOrderDetail(List<OrderDetail> orderDetails) {
        List<OrderDetailResponse> lstResponse = new ArrayList<>();
        for (OrderDetail orderDetail: orderDetails) {
            OrderDetailResponse orderDetailResponse = orderDetailMapper.entityToResponse(orderDetail);
            ProductDetailResponse productDetail = productDetailService.findById(orderDetail.getProductDetail().getId());
            orderDetailResponse.setProductDetail(productDetail);

            BigDecimal currentPrice = productDetail.getPrice();
            BigDecimal purchasePrice = orderDetailResponse.getDiscountPrice();
            if (purchasePrice != null && purchasePrice.compareTo(currentPrice) < 0) {
                orderDetailResponse.setPurchasePrice(purchasePrice);
            }

            lstResponse.add(orderDetailResponse);
        }

        return lstResponse;
    }
    private Boolean checkOrderHasPromotion(List<OrderDetail> orderDetails) {
        for (OrderDetail orderDetail: orderDetails) {
           if (orderDetail.getDiscountPrice() != null) {
               return true;
           }
        }
        return false;
    }
    @Override
    @Transactional
    public void updateQuantityOrderDetail(OrderDetailRequest request) {
        User user = f4KUtils.getUserOrNull();
//        User user = f4KUtils.getUser();

        Order order =  orderRepository.findById(request.getOrderId()).orElseThrow(
                () -> new BadRequestException("Không tồn tại hoá đơn")
        );

        if (order.getStatus() != HoaDonUtils.TrangThaiHoaDon.CHO_XAC_NHAN.getStatus()) {
            throw new BadRequestException(String.format("Hoá đơn đang ở trạng thái %s không thể thao tác", HoaDonUtils.TrangThaiHoaDon.getMessByStatus(order.getStatus())));
        }

        OrderDetail orderDetail = orderDetailRepository.findByProductDetailIdAndOrderId(order.getId(), request.getProductDetailId());
        if (orderDetail == null) {
            throw new BadRequestException("Không tồn tại hoá đơn chi tiết");
        }

        if (request.getQuantity() > orderDetail.getProductDetail().getQuantity()) {
            throw new BadRequestException("Số lượng sản phẩm không đủ");
        }
        if (request.getQuantity() < 0) {
            throw new BadRequestException("Số lượng sản phẩm không thể nhỏ hơn 0");
        }

        // Khi cập nhật lại không lấy theo promotion mà lấy theo giá lúc mua lưu trong db?
        // Promotion promotion = promotionService.getBestPromotionForProductDetail(request.getProductDetailId());
        // if (promotion != null) {
        //     BigDecimal totalPay = promotionService.calculateDiscountedPrice(orderDetail.getProductDetail().getPrice() ,promotion.getDiscountValue());
        //     orderDetail.setPrice(totalPay.divide(BigDecimal.valueOf(request.getQuantity())));
        //     orderDetail.setQuantity(request.getQuantity());
        // } else {
        //     BigDecimal totalPay = orderDetail.getProductDetail().getPrice();
        //     orderDetail.setPrice(totalPay.divide(BigDecimal.valueOf(request.getQuantity())));
        //     orderDetail.setQuantity(request.getQuantity());
        // }

        orderDetail.setQuantity(request.getQuantity());

        orderDetailRepository.save(orderDetail);
        updateTotalOrder(order);
        orderHistoryService.insertOrderHistory(order, order.getStatus(), "Cập nhật số lượng sản phẩm", user);
    }

    @Override
    @Transactional
    public void updateStatusOrder(Long orderId, Integer newStatus, String note) {
        Order order =  orderRepository.findById(orderId).orElseThrow(
                () -> new BadRequestException("Không tồn tại hoá đơn")
        );

        // Cập nhật lại số lượng theo trạng thái tương ứng
        // Từ 5 -> 8 (CHO_XAC_NHAN -> CHO_LAY_HANG)
        if (newStatus == HoaDonUtils.TrangThaiHoaDon.CHO_LAY_HANG.getStatus()
                && order.getStatus() == HoaDonUtils.TrangThaiHoaDon.CHO_XAC_NHAN.getStatus()) {
            updateProductDetail(order, true);

            // Từ 8 -> 5 (CHO_LAY_HANG -> CHO_XAC_NHAN)
        } else if (newStatus == HoaDonUtils.TrangThaiHoaDon.CHO_XAC_NHAN.getStatus()
                && order.getStatus() == HoaDonUtils.TrangThaiHoaDon.CHO_LAY_HANG.getStatus()) {
            updateProductDetail(order, false);

            // btn huỷ hoá đơn khi trạng thái hoá đơn hiện tại != 5
        } else if (newStatus == HoaDonUtils.TrangThaiHoaDon.HUY_DON.getStatus()
                && order.getStatus() != HoaDonUtils.TrangThaiHoaDon.CHO_XAC_NHAN.getStatus()) {
            updateProductDetail(order, false);
        }

        //TODO: Cần thêm check trạng thái phù hợp trc khi update
        order.setStatus(newStatus);
        if (newStatus == HoaDonUtils.TrangThaiHoaDon.HUY_DON.getStatus()) {
            order.setNote(note);
        }
        orderRepository.save(order);

        orderHistoryService.insertOrderHistory(order, order.getStatus(), HoaDonUtils.TrangThaiHoaDon.getNoteByStatus(order.getStatus()), f4KUtils.getUser());
    }

    @Override
    public void refreshOrder(Long orderId) {
        Order order =  orderRepository.findById(orderId).orElseThrow(
                () -> new BadRequestException("Không tồn tại hoá đơn")
        );
        updateTotalOrder(order);
    }

    private void updateProductDetail(Order order, boolean isSubQuantity) {
        List<OrderDetail> listOrderDetail = orderDetailRepository.findOrderDetailsByOrderId(order.getId());
        for (OrderDetail orderDetail: listOrderDetail) {
            ProductDetail productDetail = productDetailRepository.findById(orderDetail.getProductDetail().getId()).orElse(null);
            if (productDetail == null) continue;

            int quantity;
            if (isSubQuantity) {
                quantity = productDetail.getQuantity() - orderDetail.getQuantity();
                if (quantity < 0) {
                    throw new BadRequestException("Số lượng sản phẩm không đủ để cập nhật, vui lòng xem lại!");
                }
            } else {
                quantity = productDetail.getQuantity() + orderDetail.getQuantity();
            }
            productDetail.setQuantity(quantity);
            productDetailRepository.save(productDetail);
        }
    }

    private void updateTotalOrder(Order order) {
        BigDecimal shippingPay = order.getShippingPay();
        BigDecimal totalCart = BigDecimal.ZERO;
        List<OrderDetail> listOrderDetail = orderDetailRepository.findProductDetailsByOrderId(order.getId());

        for (OrderDetail orderDetail: listOrderDetail) {
            if (orderDetail.getDiscountPrice() != null) {
                totalCart = totalCart.add(orderDetail.getDiscountPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity())));
            } else {
                totalCart = totalCart.add(orderDetail.getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity())));
            }
        }

        order.setTotalPay(totalCart);
        order.setShippingPay(shippingPay);

        orderRepository.save(order);
    }
    // Code từ đây xuống cấm ngắt bên trên

}
