package org.example.quan_ao_f4k.repository.address;

import org.example.quan_ao_f4k.model.address.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long>,
		JpaSpecificationExecutor<Ward> {
	@Query("SELECT w FROM Ward w WHERE w.district.id = :districtId")
	List<Ward> findByDistrictId(@Param("districtId") Long districtId);
}
