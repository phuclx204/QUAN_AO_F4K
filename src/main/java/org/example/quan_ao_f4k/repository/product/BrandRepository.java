package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.dto.response.product.BrandResponse;
import org.example.quan_ao_f4k.model.product.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long>,
		JpaSpecificationExecutor<Brand> {
	boolean existsByName(String name);
	boolean existsByNameAndIdNot(String name,Long id);
	List<Brand> findByStatus(int status);
}
