package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>,
		JpaSpecificationExecutor<Product> {

	@Query("SELECT p from Product p where p.category.id = ?1")
	List<Product> findByCategoryId(Long categoryId);
}
