package org.example.quan_ao_f4k.repository.order;

import org.example.quan_ao_f4k.model.order.Order;
import org.example.quan_ao_f4k.model.order.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long>,
        JpaSpecificationExecutor<OrderHistory> {

    @Query("SELECT h FROM OrderHistory h WHERE h.order.id = :orderId order by h.id desc")
    List<OrderHistory> findByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT h FROM OrderHistory h WHERE h.order.code = :code order by h.id desc")
    List<OrderHistory> findByOrderCode(@Param("code") String code);
}
