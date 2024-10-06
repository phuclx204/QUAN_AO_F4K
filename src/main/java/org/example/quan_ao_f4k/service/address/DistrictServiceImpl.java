package org.example.quan_ao_f4k.service.address;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.address.DistrictRequest;
import org.example.quan_ao_f4k.dto.response.address.DistrictResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.address.DistrictMapper;
import org.example.quan_ao_f4k.repository.address.DistrictRepository;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DistrictServiceImpl implements DistrictService {
	private DistrictMapper districtMapper;
	private DistrictRepository districtRepository;

	@Override
	public ListResponse<DistrictResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
		return defaultFindAll(page,size,sort,filter,search,all, SearchFields.DISTRICT,districtRepository,districtMapper);
	}

	@Override
	public DistrictResponse findById(Long aLong) {
		return findById(aLong);
	}

	@Override
	public DistrictResponse save(DistrictRequest request) {
		return defaultSave(request,districtRepository,districtMapper);
	}

	@Override
	public DistrictResponse save(Long aLong, DistrictRequest request) {
		return defaultSave(aLong,request,districtRepository,districtMapper,"");
	}

	@Override
	public void delete(Long aLong) {
		districtRepository.deleteById(aLong);
	}

	@Override
	public void delete(List<Long> longs) {
		districtRepository.deleteAllById(longs);
	}
}
