package org.example.quan_ao_f4k.service.address;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.address.ProvinceRequest;
import org.example.quan_ao_f4k.dto.response.address.ProvinceResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.address.ProvinceMapper;
import org.example.quan_ao_f4k.repository.address.ProvinceRepository;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProvinceServiceImpl implements ProvinceService {
	private ProvinceMapper provinceMapper;
	private ProvinceRepository provinceRepository;

	@Override
	public ListResponse<ProvinceResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
		return defaultFindAll(page,size,sort,filter,search,all, SearchFields.PROVINCE,provinceRepository,provinceMapper);
	}

	@Override
	public ProvinceResponse findById(Long aLong) {
		return findById(aLong);
	}

	@Override
	public ProvinceResponse save(ProvinceRequest request) {
		return defaultSave(request,provinceRepository,provinceMapper);
	}

	@Override
	public ProvinceResponse save(Long aLong, ProvinceRequest request) {
		return defaultSave(aLong,request,provinceRepository,provinceMapper,"");
	}

	@Override
	public void delete(Long aLong) {
		provinceRepository.deleteById(aLong);
	}

	@Override
	public void delete(List<Long> longs) {
		provinceRepository.deleteAllById(longs);
	}
}
