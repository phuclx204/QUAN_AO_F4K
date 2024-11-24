package org.example.quan_ao_f4k.service.users;

import org.example.quan_ao_f4k.dto.request.users.EmployeeRequest;
import org.example.quan_ao_f4k.dto.response.users.EmployeeResponse;
import org.example.quan_ao_f4k.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface EmployeeService extends CrudService<Long, EmployeeRequest, EmployeeResponse> {
	Page<EmployeeResponse> findEmployeesByStaffRole(int page, int size, String search);

}
