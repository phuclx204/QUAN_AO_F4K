package org.example.quan_ao_f4k.service.order;


import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.order.OrderDetailRequest;
import org.example.quan_ao_f4k.dto.request.order.OrderDetailResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.order.OrderDetailMapper;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.model.order.OrderProductDetailKey;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.example.quan_ao_f4k.repository.order.OrderDetailRepository;
import org.example.quan_ao_f4k.repository.product.ProductDetailRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class OrderDetailServiceimpl implements OrderDetailService {

    private final OrderDetailMapper orderDetailMapper;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductDetailRepository productDetailRepository;

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

        OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);
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

}

