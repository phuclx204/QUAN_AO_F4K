package org.example.quan_ao_f4k.repository.general;

import org.example.quan_ao_f4k.model.general.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long>,
		JpaSpecificationExecutor<Image> {

	@Query("SELECT a FROM Image a WHERE a.idParent = ?1 and a.tableCode = ?2")
	List<Image> getImageByIdParent(Long id, String tableCode);

	@Query("SELECT a FROM Image a WHERE a.idParent = ?1 and a.tableCode = ?2 order by a.idParent asc limit 1")
	Image findImageByIdParent(Long id, String tableCode);

	@Modifying
	@Query("DELETE FROM Image a WHERE a.idParent = ?1 AND a.tableCode = ?2")
	void deleteImageByIdParent(Long id, String tableCode);

	@Modifying
	@Query("DELETE FROM Image a WHERE a.idParent = ?1 AND a.tableCode = ?2 AND a.id NOT IN ?3")
	void deleteImagesByParentIdAndTableCodeNotIn(Long idParent, String tableCode, List<Long> ids);
}
