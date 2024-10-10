package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.Category;
import org.example.quan_ao_f4k.model.product.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ColorRepository extends JpaRepository<Color, Long>,
		JpaSpecificationExecutor<Color> {

	@Query("SELECT c FROM Color c where c.name = ?1")
	public Color findByName(String name);
}
