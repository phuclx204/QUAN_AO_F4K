package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ColorRepository extends JpaRepository<Color, Long>,
		JpaSpecificationExecutor<Color> {
	boolean existsByName(String name);
	boolean existsByNameAndIdNot(String name,Long id);
	List<Color> findByStatus(int status);

	// ==== sonng - shop site - start ====
	@Query("select c from Color c left join ProductDetail p on c.id = p.color.id where p.product.slug = :slug")
	List<Color> findBySlugProduct(@Param("slug") String slug);
	// ==== sonng - shop site - end ====
}
