package org.example.quan_ao_f4k.repository.promotion;

import org.example.quan_ao_f4k.model.promotion.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long>,
		JpaSpecificationExecutor<Promotion> {
}
