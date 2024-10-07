package org.example.quan_ao_f4k.mapper.address;

import org.example.quan_ao_f4k.dto.request.address.DistrictRequest;
import org.example.quan_ao_f4k.dto.response.address.DistrictResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.mapper.MapperCoverter;
import org.example.quan_ao_f4k.model.address.District;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE
	,uses = {MapperCoverter.class,ProvinceMapper.class}
)
public interface DistrictMapper extends GennericMapper<District, DistrictRequest, DistrictResponse> {
	@Override
	@Mapping(source = "provinceId", target = "province", qualifiedByName = "convertToProvince")
	District requestToEntity(DistrictRequest request);
}
