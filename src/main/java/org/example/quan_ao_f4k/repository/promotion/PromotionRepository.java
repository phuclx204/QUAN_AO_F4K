package org.example.quan_ao_f4k.repository.promotion;

import org.example.quan_ao_f4k.dto.request.promotion.PromotionRequest;
import org.example.quan_ao_f4k.model.promotion.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long>,
		JpaSpecificationExecutor<Promotion> {

	@Query("SELECT p FROM Promotion p " +
			"WHERE (:name IS NULL OR p.name LIKE CONCAT('%', :name, '%')) " +
			"AND (:status IS NULL OR p.status = :status) " +
			"AND (:effectiveDate IS NULL " +
			"OR (:effectiveDate = 1 AND current_date BETWEEN p.dayStart AND p.dayEnd) " +
			"OR (:effectiveDate = 2 AND current_date NOT BETWEEN p.dayStart AND p.dayEnd))")
	List<Promotion> findPromotionsByRequest(String name,
											Integer status,
											Integer effectiveDate);

	@Query("SELECT p FROM Promotion p " +
			"LEFT JOIN PromotionProduct o ON p.id = o.promotion.id " +
			"WHERE (?1 BETWEEN p.dayStart AND p.dayEnd) " +
			"AND p.status = 1 " +
			"AND o.product.id IN ?2")
	List<Promotion> findPromotionsByProductId(LocalDate dateStart, List<Long> productId);

	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Promotion p " +
			"WHERE p.name = :name " +
			"AND :dateStart BETWEEN p.dayStart AND p.dayEnd " +
			"AND (:id IS NULL OR :id != p.id)")
	boolean existsByNameAndDate(String name, LocalDate dateStart, Long id);
}
