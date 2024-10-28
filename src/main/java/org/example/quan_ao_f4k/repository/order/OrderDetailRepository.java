package org.example.quan_ao_f4k.repository.order;

import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.model.order.OrderProductDetailKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderProductDetailKey>,
		JpaSpecificationExecutor<OrderDetail> {

	@Query("SELECT COUNT(od) FROM OrderDetail od WHERE od.productDetail.id = :isParent")
	int countByProductDetail(@Param("isParent") Long isParent);

	@Query("SELECT o FROM OrderDetail o WHERE o.order.id = :orderId")
	List<OrderDetail> findOrderDetailsByOrderId(@Param("orderId") Long orderId);
}
