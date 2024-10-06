package org.example.quan_ao_f4k.service.address;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.address.AddressRequest;
import org.example.quan_ao_f4k.dto.response.address.AddressResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.address.AddressMapper;
import org.example.quan_ao_f4k.repository.address.AddressRepository;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService {
	private AddressMapper addressMapper;
	private AddressRepository addressRepository;

	@Override
	public ListResponse<AddressResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
		return defaultFindAll(page,size,sort,filter,search,all, SearchFields.ADDRESS,addressRepository,addressMapper);
	}

	@Override
	public AddressResponse findById(Long aLong) {
		return findById(aLong);
	}

	@Override
	public AddressResponse save(AddressRequest request) {
		return defaultSave(request,addressRepository,addressMapper);
	}

	@Override
	public AddressResponse save(Long aLong, AddressRequest request) {
		return defaultSave(aLong,request,addressRepository,addressMapper,"");
	}

	@Override
	public void delete(Long aLong) {
		addressRepository.deleteById(aLong);
	}

	@Override
	public void delete(List<Long> longs) {
		addressRepository.deleteAllById(longs);
	}
}
