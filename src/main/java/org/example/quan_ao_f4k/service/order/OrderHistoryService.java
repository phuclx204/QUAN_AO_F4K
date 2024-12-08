package org.example.quan_ao_f4k.service.order;

import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.model.order.Order;

public interface OrderHistoryService {
    void insertOrderHistory(Order order, Integer status, String note, User user);
}
