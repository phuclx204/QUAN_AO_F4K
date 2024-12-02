package org.example.quan_ao_f4k.repository.authentication;

import org.example.quan_ao_f4k.model.authentication.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long>,
		JpaSpecificationExecutor<User> {

	Optional<User> findByUsername(String username);
	List<User> findAllByUsername(String username);

	@Query("SELECT EXISTS(SELECT 1 FROM User u WHERE u.username = :username)")
	boolean existsByUsername(@Param("username") String username);

	@Query("SELECT u FROM User u where u.id = :id and u.username = :username")
	Optional<User> findByIdAndUsername(@Param("id") Long id, @RequestParam("username") String username);

	@Query("SELECT u FROM User u where u.id = :id")
	Optional<User> findByIdUser(@Param("id") Long id);
}
