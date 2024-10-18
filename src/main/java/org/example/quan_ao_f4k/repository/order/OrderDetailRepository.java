package org.example.quan_ao_f4k.repository.order;

import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.model.order.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId>,
		JpaSpecificationExecutor<OrderDetail> {
	@Query("SELECT COUNT(od) FROM OrderDetail od WHERE od.productDetail.id = :isParent")
	int countByProductDetail(@Param("isParent") Long isParent);

}
