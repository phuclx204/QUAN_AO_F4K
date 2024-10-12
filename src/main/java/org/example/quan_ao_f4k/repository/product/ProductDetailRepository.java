package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long>,
		JpaSpecificationExecutor<ProductDetail> {
	boolean existsBySizeId(Long id);
	boolean existsByColorId(Long id);
}
