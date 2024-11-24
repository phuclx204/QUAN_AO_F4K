package org.example.quan_ao_f4k.service.users;

import org.example.quan_ao_f4k.dto.request.users.CustomerRequest;
import org.example.quan_ao_f4k.dto.response.users.CustomerResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.users.CustomerMapper;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.repository.users.CustomerRepository;
import org.example.quan_ao_f4k.util.F4KUtils;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
	private final CustomerRepository customerRepository;
	private final CustomerMapper customerMapper;

	public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
		this.customerRepository = customerRepository;
		this.customerMapper = customerMapper;
	}

	@Override
	public ListResponse<CustomerResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
		return defaultFindAll(page, size, sort, filter, search, all, SearchFields.CUSTOMERS, customerRepository, customerMapper);
	}

	@Override
	public CustomerResponse findById(Long aLong) {
		return defaultFindById(aLong, customerRepository, customerMapper, "");
	}

	@Override
	public CustomerResponse save(CustomerRequest request) {
		return defaultSave(request, customerRepository, customerMapper);
	}

	@Override
	public CustomerResponse save(Long aLong, CustomerRequest request) {
		return defaultSave(aLong, request, customerRepository, customerMapper, "");
	}

	@Override
	public void delete(Long aLong) {
		customerRepository.deleteById(aLong);
	}

	@Override
	public void delete(List<Long> longs) {
		customerRepository.deleteAllById(longs);
	}

	@Override
	public Page<CustomerResponse> findCustomersByUserRole(int page, int size, String search) {
		Pageable pageable = PageRequest.of(page - 1, size);
		List<User> list = customerRepository.findCustomersByUserRole(search);
		return F4KUtils.toPage(customerMapper.entityToResponse(list),pageable);
	}
}
