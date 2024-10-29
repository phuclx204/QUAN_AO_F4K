package org.example.quan_ao_f4k.service.employee;

import org.example.quan_ao_f4k.dto.request.employee.EmployeeRequest;
import org.example.quan_ao_f4k.dto.response.employee.EmployeeResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.model.employee.Employee;
import org.example.quan_ao_f4k.service.CrudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmployeeService extends CrudService<Long, EmployeeRequest, EmployeeResponse> {
	List<Employee> findEmployeesByStaffRole();
}
