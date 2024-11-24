package org.example.quan_ao_f4k.service.users;

import org.example.quan_ao_f4k.dto.request.users.EmployeeRequest;
import org.example.quan_ao_f4k.dto.response.users.EmployeeResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.users.EmployeeMapper;
import org.example.quan_ao_f4k.model.authentication.Employee;
import org.example.quan_ao_f4k.repository.users.EmployeeRepository;
import org.example.quan_ao_f4k.util.F4KUtils;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	public Page<EmployeeResponse> findEmployeesByStaffRole(int page, int size, String search) {
		Pageable pageable = PageRequest.of(page - 1, size);
		List<Employee> list =employeeRepository.findEmployeesByStaffRole(search);
		return F4KUtils.toPage(employeeMapper.entityToResponse(list),pageable);
	}
}
