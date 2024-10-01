package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryRepository extends JpaRepository<Category, Integer>,
		JpaSpecificationExecutor<Category> {
}
