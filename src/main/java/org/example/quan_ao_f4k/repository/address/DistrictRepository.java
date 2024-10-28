package org.example.quan_ao_f4k.repository.address;

import org.example.quan_ao_f4k.model.address.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long>,
		JpaSpecificationExecutor<District> {
}
