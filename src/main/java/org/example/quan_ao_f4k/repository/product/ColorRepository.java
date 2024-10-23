package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.Brand;
import org.example.quan_ao_f4k.model.product.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ColorRepository extends JpaRepository<Color, Long>,
		JpaSpecificationExecutor<Color> {
	boolean existsByName(String name);
	boolean existsByNameAndIdNot(String name,Long id);
	List<Color> findByStatus(int status);

	@Query("select c from Color c left join ProductDetail p on c.id = p.color.id where p.product.id = ?1")
	List<Color> findByProductId(Long id);
}
