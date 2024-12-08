package org.example.quan_ao_f4k.service.order;

import lombok.RequiredArgsConstructor;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.model.order.Order;
import org.example.quan_ao_f4k.model.order.OrderHistory;
import org.example.quan_ao_f4k.repository.order.OrderHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderHistoryServiceImpl implements OrderHistoryService {

    private final OrderHistoryRepository orderHistoryRepository;

    @Override
    public void insertOrderHistory(Order order, Integer status, String note, User user) {
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setStatus(status);
        orderHistory.setNote(note);
        orderHistory.setOrder(order);
        orderHistory.setCreatedAt(LocalDateTime.now());

        if (user != null) {
            orderHistory.setCreateBy(user.getUsername());
        }

        orderHistoryRepository.save(orderHistory);
    };
}
