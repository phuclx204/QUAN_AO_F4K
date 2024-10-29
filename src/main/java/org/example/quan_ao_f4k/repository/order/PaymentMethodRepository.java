package org.example.quan_ao_f4k.repository.order;

import org.example.quan_ao_f4k.model.order.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long>,
		JpaSpecificationExecutor<PaymentMethod> {
}
