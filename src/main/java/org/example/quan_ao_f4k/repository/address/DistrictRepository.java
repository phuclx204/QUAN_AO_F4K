package org.example.quan_ao_f4k.repository.address;

import org.example.quan_ao_f4k.model.address.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long>,
		JpaSpecificationExecutor<District> {
	@Query("SELECT d FROM District d WHERE d.province.id = :provinceId")
	List<District> findByProvinceId(@Param("provinceId") Long provinceId);
}
