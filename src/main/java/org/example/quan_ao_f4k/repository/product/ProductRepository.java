package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Long>,
		JpaSpecificationExecutor<Product> {
	boolean existsByBrandId(Long id);
	boolean existsByCategoryId(Long id);
	boolean existsByNameAndIdNot(String name,Long id);
}
