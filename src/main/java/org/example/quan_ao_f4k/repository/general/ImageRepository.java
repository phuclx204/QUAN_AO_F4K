package org.example.quan_ao_f4k.repository.general;

import org.example.quan_ao_f4k.model.general.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long>,
		JpaSpecificationExecutor<Image> {

	@Query("SELECT a FROM Image a WHERE a.idParent = ?1")
	List<Image> getImageByIdParent(Long id);

	@Query("SELECT a FROM Image a WHERE a.idParent = ?1 order by a.idParent asc limit 1")
	Image findImageByIdParent(Long id);
}
