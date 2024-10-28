package org.example.quan_ao_f4k.repository.authentication;

import org.example.quan_ao_f4k.model.authentication.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>,
		JpaSpecificationExecutor<Role> {

	Role findByName(String name);
}
