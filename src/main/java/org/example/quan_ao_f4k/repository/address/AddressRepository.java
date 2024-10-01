package org.example.quan_ao_f4k.repository.address;

import org.example.quan_ao_f4k.model.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AddressRepository extends JpaRepository<Address, Long>,
		JpaSpecificationExecutor<Address> {
}
