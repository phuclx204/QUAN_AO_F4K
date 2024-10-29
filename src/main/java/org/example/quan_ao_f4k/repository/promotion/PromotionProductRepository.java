package org.example.quan_ao_f4k.repository.promotion;

import org.example.quan_ao_f4k.model.promotion.PromotionProduct;
import org.example.quan_ao_f4k.model.promotion.PromotionProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PromotionProductRepository extends JpaRepository<PromotionProduct, PromotionProductId>,
		JpaSpecificationExecutor<PromotionProduct> {

	void deleteAllByPromotion_Id(Long id);

	@Query("SELECT p from PromotionProduct p where p.promotion.id = ?1 and (?2 is null or p.status = ?2)")
	List<PromotionProduct> findByPromotionId(long promotionId, Integer status);
}
