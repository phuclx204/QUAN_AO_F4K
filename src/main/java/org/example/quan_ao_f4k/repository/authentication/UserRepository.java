package org.example.quan_ao_f4k.repository.authentication;

import org.example.quan_ao_f4k.model.authentication.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>,
		JpaSpecificationExecutor<User> {

	Optional<User> findByUsername(String username);
	boolean existsByUsername(String username);
}
