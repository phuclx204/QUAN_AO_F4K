package org.example.quan_ao_f4k.repository.authentication;

import org.example.quan_ao_f4k.model.authentication.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long>,
		JpaSpecificationExecutor<User> {

	@Query("SELECT u FROM User u WHERE u.username = ?1 AND u.password = ?2")
	User findByUsernameAndPassword(String username, String password);
}
