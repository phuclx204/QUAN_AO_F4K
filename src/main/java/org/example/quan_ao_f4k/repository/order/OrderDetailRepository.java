package org.example.quan_ao_f4k.repository.order;

import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer>,
		JpaSpecificationExecutor<OrderDetail> {
}
