package org.example.quan_ao_f4k.service.order;

import org.example.quan_ao_f4k.dto.request.order.OrderRequest;
import org.example.quan_ao_f4k.dto.request.order.OrderResponse;
import org.example.quan_ao_f4k.service.CrudService;

public interface OrderService extends CrudService<Long, OrderRequest, OrderResponse> {
}
