package org.example.quan_ao_f4k.repository.promotion;

import org.example.quan_ao_f4k.model.promotion.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long>,
		JpaSpecificationExecutor<Promotion> {

	@Query("SELECT p FROM Promotion p " +
			"WHERE (:name IS NULL OR p.name LIKE CONCAT('%', :name, '%')) " +
			"AND (:status IS NULL OR p.status = :status) " +
			"AND (:effectiveDate IS NULL " +
			"OR (:effectiveDate = 1 AND current_date BETWEEN p.dayStart AND p.dayEnd) " +
			"OR (:effectiveDate = 2 AND current_date NOT BETWEEN p.dayStart AND p.dayEnd))" +
			"ORDER BY p.id desc ")
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
			"WHERE (p.name is null or p.name = :name) " +
			"AND :dateStart BETWEEN p.dayStart AND p.dayEnd " +
			"AND (:id IS NULL OR :id != p.id)")
	boolean existsByNameAndDate(String name, LocalDate dateStart, Long id);

	@Query("SELECT p FROM Promotion p " +
			"WHERE (p.name is null or p.name = :name) " +
			"AND :dateStart BETWEEN p.dayStart AND p.dayEnd " +
			"AND (:id IS NULL OR :id != p.id)")
	Optional<Promotion> findByNameAndDate(String name, LocalDate dateStart, Long id);

	@Query("SELECT p FROM Promotion p " +
			"WHERE :dateStart BETWEEN p.dayStart AND p.dayEnd " +
			"AND (:id IS NULL OR :id != p.id)" +
			"order by p.id desc limit 1")
	Optional<Promotion> findByStarDate(LocalDate dateStart, Long id);

	@Query("SELECT p FROM Promotion p WHERE p.status = :status " +
			"AND p.dayStart <= :now AND p.dayEnd >= :now")
	List<Promotion> findAllByStatusAndDayStartBeforeAndDayEndAfter(
			@Param("status") Integer status,
			@Param("now") LocalDate now);
}
