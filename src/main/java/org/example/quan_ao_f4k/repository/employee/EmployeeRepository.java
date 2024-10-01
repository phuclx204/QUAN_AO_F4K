package org.example.quan_ao_f4k.repository.employee;

import org.example.quan_ao_f4k.model.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmployeeRepository extends JpaRepository<Employee, Long>,
		JpaSpecificationExecutor<Employee> {
}
