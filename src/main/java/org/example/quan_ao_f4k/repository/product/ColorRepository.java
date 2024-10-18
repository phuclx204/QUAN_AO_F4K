package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.Brand;
import org.example.quan_ao_f4k.model.product.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ColorRepository extends JpaRepository<Color, Long>,
		JpaSpecificationExecutor<Color> {
	boolean existsByName(String name);
	boolean existsByNameAndIdNot(String name,Long id);
	List<Color> findByStatus(int status);

}
