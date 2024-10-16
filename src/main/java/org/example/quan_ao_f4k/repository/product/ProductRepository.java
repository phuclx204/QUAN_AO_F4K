package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>,
		JpaSpecificationExecutor<Product> {
	@Query("SELECT p.name FROM Product p WHERE p.brand.id = :brandId AND p.category.id = :categoryId")
	List<String> findProductNamesByBrandAndCategory(@Param("brandId") Long brandId, @Param("categoryId") Long categoryId);

}
