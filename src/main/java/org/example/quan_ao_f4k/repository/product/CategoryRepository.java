package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>,
		JpaSpecificationExecutor<Category> {
	boolean existsByName(String name);
	boolean existsByNameAndIdNot(String name,Long id);
	List<Category> findByStatus(int status);

}
