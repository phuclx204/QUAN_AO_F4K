package org.example.quan_ao_f4k.repository.order;

import org.example.quan_ao_f4k.dto.response.orders.OrderStatisticsResponse;
import org.example.quan_ao_f4k.dto.response.product.ProductDetailDTO;
import org.example.quan_ao_f4k.model.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>,
		JpaSpecificationExecutor<Order> {
	@Query("SELECT o FROM Order o WHERE o.code = :code")
	Order findOrderByOrderCode(@Param("code") String code);

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

	@Query("SELECT COALESCE(SUM(o.totalPay), 0) FROM Order o WHERE o.status = 3")
	BigDecimal getTotalPay();

	@Query("SELECT COUNT(o.id) FROM Order o WHERE o.status = 3")
	Integer getTotalQuantityOrders();

	@Query("SELECT COALESCE(SUM(od.quantity), 0) " +
			"FROM Order o JOIN OrderDetail od ON o.id = od.order.id " +
			"WHERE o.status = 3")
	Integer getTotalProductQuantityInCompletedOrders();

	@Query("SELECT o.order_type, COALESCE(SUM(o.totalPay), 0) " +
			"FROM Order o " +
			"WHERE o.order_type IN ('OFFLINE', 'ONLINE') AND o.status = 3 " +
			"GROUP BY o.order_type")
	List<Object[]> getTotalPayByOrderType();

	@Query("SELECT o from Order o where o.id = :orderId")
	Optional<Order> findByOrderId(@Param("orderId") Long orderId);

	@Query("SELECT o from Order o where o.id = :orderId")
	Order findAllById(@Param("orderId") Long orderId);

}
