package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SizeRepository extends JpaRepository<Size, Long>,
		JpaSpecificationExecutor<Size> {
	boolean existsByName(String name);
	boolean existsByNameAndIdNot(String name,Long id);
	List<Size> findByStatus(int status);

	// ==== sonng - shop site - start ====
	@Query("select s from Size s left join ProductDetail p on s.id = p.size.id  where p.product.slug = :slug")
	List<Size> findBySlugProduct(@Param("slug") String slug);
	// ==== sonng - shop site - end ====
}
