package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.Brand;
import org.example.quan_ao_f4k.model.product.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SizeRepository extends JpaRepository<Size, Long>,
		JpaSpecificationExecutor<Size> {
	boolean existsByName(String name);
	boolean existsByNameAndIdNot(String name,Long id);
	List<Size> findByStatus(int status);

}
