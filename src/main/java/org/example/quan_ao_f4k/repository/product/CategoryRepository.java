package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long>,
		JpaSpecificationExecutor<Category> {

	@Query("SELECT c FROM Category c where c.name = ?1")
	public Category findByName(String name);
}
