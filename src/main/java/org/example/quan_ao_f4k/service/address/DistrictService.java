package org.example.quan_ao_f4k.service.address;

import org.example.quan_ao_f4k.dto.request.address.DistrictRequest;
import org.example.quan_ao_f4k.dto.response.address.DistrictResponse;
import org.example.quan_ao_f4k.service.CrudService;
import org.springframework.stereotype.Service;

@Service
public interface DistrictService extends CrudService<Long, DistrictRequest, DistrictResponse> {
}
