package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long>,
		JpaSpecificationExecutor<ProductDetail> {

	@Query("SELECT p from ProductDetail p where p.color.id = ?1")
	List<ProductDetail> findByColorId(Long id);
}
