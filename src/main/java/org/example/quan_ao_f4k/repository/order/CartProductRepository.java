package org.example.quan_ao_f4k.repository.order;

import org.example.quan_ao_f4k.model.order.CartProduct;
import org.example.quan_ao_f4k.model.order.CartProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, CartProductId>, JpaSpecificationExecutor<CartProduct> {

	@Query("SELECT COUNT(cp) FROM CartProduct cp WHERE cp.productDetail.id = :isParent")
	int countByProductDetail(@Param("isParent") Long isParent);

	List<CartProduct> findAllByCart_Id(Long id);
}
