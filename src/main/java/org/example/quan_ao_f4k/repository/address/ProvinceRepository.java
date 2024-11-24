package org.example.quan_ao_f4k.repository.address;

import org.example.quan_ao_f4k.model.address.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long>,
		JpaSpecificationExecutor<Province> {
	@Query("SELECT p FROM Province p")
	List<Province> findAllProvinces();
}
