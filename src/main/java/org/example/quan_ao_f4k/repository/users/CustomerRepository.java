package org.example.quan_ao_f4k.repository.users;

import org.example.quan_ao_f4k.model.authentication.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<User, Long>,
		JpaSpecificationExecutor<User> {

	@Query("""
           SELECT u 
           FROM User u 
           WHERE u.role.name = 'USER' 
           AND (:search IS NULL OR 
                LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR 
                LOWER(u.numberPhone) LIKE LOWER(CONCAT('%', :search, '%')) OR 
                LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
           ORDER BY u.id DESC
           """)
	List<User> findCustomersByUserRole(String search);
}
