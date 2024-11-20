package org.example.quan_ao_f4k.repository.order;

import jakarta.transaction.Transactional;
import org.example.quan_ao_f4k.dto.response.orders.OrderResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.model.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {

    @Query("SELECT o FROM Order o WHERE o.order_type = :orderType AND o.status = :status")
    List<Order> findOrdersByStatus(@Param("orderType") String orderType, @Param("status") Integer status);

    @Query("SELECT o FROM Order o WHERE "
            + "(o.status != 1) AND "
            + "(o.createdAt >= :startDate OR :startDate IS NULL) AND "
            + "(o.createdAt <= :endDate OR :endDate IS NULL) AND "
            + "(o.code LIKE %:search% OR :search IS NULL OR "
            + "o.toName LIKE %:search% OR :search IS NULL OR "
            + "o.toPhone LIKE %:search% OR :search IS NULL OR "
            + "o.order_type LIKE %:search% OR :search IS NULL) AND "
            + "(o.status = :status OR :status IS NULL)")
    Page<Order> searchOrders(Pageable pageable,
                             @Param("startDate") LocalDateTime startDate,
                             @Param("endDate") LocalDateTime endDate,
                             @Param("search") String search,
                             @Param("status") Integer status);


    // sonng - shop site
    @Query("select o from Order o where o.code = :orderCode and o.user.id = :userId")
    Optional<Order> findByCodeAAndUser_Id(@Param("orderCode") String orderCode, @Param("userId") Long userId);
}