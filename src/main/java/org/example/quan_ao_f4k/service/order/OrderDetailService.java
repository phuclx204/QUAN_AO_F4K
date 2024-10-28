package org.example.quan_ao_f4k.service.order;

import org.example.quan_ao_f4k.dto.request.order.OrderDetailRequest;
import org.example.quan_ao_f4k.dto.request.order.OrderDetailResponse;
import org.example.quan_ao_f4k.model.order.OrderProductDetailKey;
import org.example.quan_ao_f4k.service.CrudService;
import org.springframework.stereotype.Service;

@Service
public interface OrderDetailService extends CrudService<OrderProductDetailKey, OrderDetailRequest, OrderDetailResponse> {
}
