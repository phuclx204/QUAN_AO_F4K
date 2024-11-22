package org.example.quan_ao_f4k.repository.order;

import jakarta.transaction.Transactional;
import org.example.quan_ao_f4k.model.order.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>,
		JpaSpecificationExecutor<Cart> {

	@Query("SELECT c FROM Cart c where c.user.id = ?1 order by c.user.id limit 1")
	public Optional<Cart> findByUserId(Long id);

	//sonng - shop site
	@Modifying
	@Transactional
	@Query("DELETE FROM Cart c WHERE c.user.id = :userId")
	void deleteAllByUser_Id(@Param("userId") Long userId);
}
