package org.example.quan_ao_f4k.service.order;


import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.order.OrderDetailRequest;
import org.example.quan_ao_f4k.dto.request.order.OrderDetailResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.order.OrderDetailMapper;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.model.order.OrderProductDetailKey;
import org.example.quan_ao_f4k.repository.order.OrderDetailRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class OrderDetailServiceimpl implements OrderDetailService {

    private final OrderDetailMapper orderDetailMapper;
    private final OrderDetailRepository orderDetailRepository;

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
}
