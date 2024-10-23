package org.example.quan_ao_f4k.repository.order;

import org.example.quan_ao_f4k.model.order.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long>,
		JpaSpecificationExecutor<Cart> {

	@Query("SELECT c FROM Cart c where c.user.id = ?1 order by c.user.id limit 1")
	public Optional<Cart> findByUserId(Long id);
}
