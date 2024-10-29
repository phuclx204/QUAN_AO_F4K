package org.example.quan_ao_f4k.repository.employee;

import org.example.quan_ao_f4k.dto.response.employee.EmployeeResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.model.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>,
		JpaSpecificationExecutor<Employee> {

	@Query("SELECT e FROM Employee e " +
			"JOIN e.user u " +
			"JOIN u.role r " +
			"WHERE r.name = 'STAFF'")
	List<Employee> findEmployeesByStaffRole();

}
