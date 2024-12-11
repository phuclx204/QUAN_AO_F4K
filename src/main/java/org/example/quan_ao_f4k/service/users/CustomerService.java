package org.example.quan_ao_f4k.service.users;

import org.example.quan_ao_f4k.dto.request.users.CustomerRequest;
import org.example.quan_ao_f4k.dto.response.users.CustomerResponse;
import org.example.quan_ao_f4k.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface CustomerService extends CrudService<Long, CustomerRequest, CustomerResponse> {
	Page<CustomerResponse> searchCustomer(int page,int size,String search);

	Page<CustomerResponse> searchAccount(int page,int size,String search);

	void saveCustoms(CustomerRequest request);
}
