package org.example.quan_ao_f4k.service.address;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.address.WardRequest;
import org.example.quan_ao_f4k.dto.response.address.WardResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.address.WardMapper;
import org.example.quan_ao_f4k.repository.address.WardRepository;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class WardServiceImpl implements WardService {
	private WardMapper wardMapper;
	private WardRepository wardRepository;

	@Override
	public ListResponse<WardResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
		return defaultFindAll(page,size,sort,filter,search,all, SearchFields.WARD,wardRepository,wardMapper);
	}

	@Override
	public WardResponse findById(Long aLong) {
		return findById(aLong);
	}

	@Override
	public WardResponse save(WardRequest request) {
		return defaultSave(request,wardRepository,wardMapper);
	}

	@Override
	public WardResponse save(Long aLong, WardRequest request) {
		return defaultSave(aLong,request,wardRepository,wardMapper,"");
	}

	@Override
	public void delete(Long aLong) {
		wardRepository.deleteById(aLong);
	}

	@Override
	public void delete(List<Long> longs) {
		wardRepository.deleteAllById(longs);
	}
}
