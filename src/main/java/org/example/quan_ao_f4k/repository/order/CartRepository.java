package org.example.quan_ao_f4k.repository.order;

import org.example.quan_ao_f4k.model.order.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CartRepository extends JpaRepository<Cart, Long>,
		JpaSpecificationExecutor<Cart> {
}
