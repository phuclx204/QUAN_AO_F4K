package org.example.quan_ao_f4k.repository.users;

import org.example.quan_ao_f4k.model.authentication.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>,
		JpaSpecificationExecutor<Employee> {

	@Query("""
       SELECT e 
       FROM Employee e 
       JOIN e.user u 
       JOIN u.role r 
       WHERE r.name = 'STAFF' 
       AND (:search IS NULL OR 
            LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR 
            LOWER(u.numberPhone) LIKE LOWER(CONCAT('%', :search, '%')) OR 
            LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
       """)
	List<Employee> findEmployeesByStaffRole(@Param("search") String search);
}
