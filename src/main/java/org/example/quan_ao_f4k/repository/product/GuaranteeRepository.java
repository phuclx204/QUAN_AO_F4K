package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.Guarantee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GuaranteeRepository extends JpaRepository<Guarantee, Long>,
		JpaSpecificationExecutor<Guarantee> {
	boolean existsByName(String name);
	boolean existsByNameAndIdNot(String name,Long id);
}
