package org.example.quan_ao_f4k.service.employee;

import org.example.quan_ao_f4k.dto.request.employee.EmployeeRequest;
import org.example.quan_ao_f4k.dto.response.employee.EmployeeResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.employee.EmployeeMapper;
import org.example.quan_ao_f4k.model.employee.Employee;
import org.example.quan_ao_f4k.repository.employee.EmployeeRepository;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService{
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private EmployeeMapper employeeMapper;

	@Override
	public ListResponse<EmployeeResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
		return defaultFindAll(page,size,sort,filter,search,all, SearchFields.EMPLOYEE,employeeRepository,employeeMapper);
	}

	@Override
	public EmployeeResponse findById(Long aLong) {
		return defaultFindById(aLong,employeeRepository,employeeMapper,"");
	}

	@Override
	public EmployeeResponse save(EmployeeRequest request) {
		return defaultSave(request,employeeRepository,employeeMapper);
	}

	@Override
	public EmployeeResponse save(Long aLong, EmployeeRequest request) {
		return defaultSave(aLong,request,employeeRepository,employeeMapper,"");
	}

	@Override
	public void delete(Long aLong) {
		employeeRepository.deleteById(aLong);
	}

	@Override
	public void delete(List<Long> longs) {
		employeeRepository.deleteAllById(longs);
	}

	@Override
	public List<Employee> findEmployeesByStaffRole() {
		return employeeRepository.findEmployeesByStaffRole();
	}
}
