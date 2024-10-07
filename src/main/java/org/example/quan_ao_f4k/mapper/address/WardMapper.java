package org.example.quan_ao_f4k.mapper.address;

import org.example.quan_ao_f4k.dto.request.address.ProvinceRequest;
import org.example.quan_ao_f4k.dto.request.address.WardRequest;
import org.example.quan_ao_f4k.dto.response.address.WardResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.mapper.MapperCoverter;
import org.example.quan_ao_f4k.model.address.Province;
import org.example.quan_ao_f4k.model.address.Ward;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
	uses = {MapperCoverter.class,DistrictMapper.class}
)
public interface WardMapper extends GennericMapper<Ward, WardRequest, WardResponse> {
	@Override
	@Mapping(source = "districtId", target = "district", qualifiedByName = "convertToDistrict")
	Ward requestToEntity(WardRequest request);
}
