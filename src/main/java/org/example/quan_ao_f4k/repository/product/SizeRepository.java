package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.Color;
import org.example.quan_ao_f4k.model.product.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface SizeRepository extends JpaRepository<Size, Long>,
		JpaSpecificationExecutor<Size> {

	@Query("SELECT c FROM Size c where c.name = ?1")
	public Size findByName(String name);
}
