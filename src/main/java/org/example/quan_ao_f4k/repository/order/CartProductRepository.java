package org.example.quan_ao_f4k.repository.order;

import jakarta.transaction.Transactional;
import org.example.quan_ao_f4k.model.order.CartProduct;
import org.example.quan_ao_f4k.model.order.CartProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, CartProductId>, JpaSpecificationExecutor<CartProduct> {

	@Query("SELECT COUNT(cp) FROM CartProduct cp WHERE cp.productDetail.id = :isParent")
	int countByProductDetail(@Param("isParent") Long isParent);

	// for shopping site
	List<CartProduct> findAllByCart_Id(Long id);
	Optional<CartProduct> findByCart_IdAndProductDetail_Id(Long cartId, Long productDetailId);
	@Query("SELECT COUNT(cp) FROM CartProduct cp WHERE cp.cart.id = :cardId")
	int countProductDetailByCartId(@Param("cardId") Long cardId);

	@Modifying
	@Transactional
	@Query("DELETE FROM CartProduct cp WHERE cp.cart.user.id = :userId")
	void deleteAllByUser_Id(@Param("userId") Long userId);
}
