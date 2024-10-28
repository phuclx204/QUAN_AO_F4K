package org.example.quan_ao_f4k.repository.order;

import org.example.quan_ao_f4k.model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {

    @Query("SELECT o FROM Order o WHERE o.order_type = :orderType AND o.status = :status")
    List<Order> findOrdersByStatus(@Param("orderType") String orderType, @Param("status") Integer status);

}