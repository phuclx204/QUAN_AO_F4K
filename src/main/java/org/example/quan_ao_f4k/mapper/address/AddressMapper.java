package org.example.quan_ao_f4k.mapper.address;

import org.example.quan_ao_f4k.dto.request.address.AddressRequest;
import org.example.quan_ao_f4k.dto.response.address.AddressResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.mapper.MapperCoverter;
import org.example.quan_ao_f4k.model.address.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE
	,uses = {MapperCoverter.class,ProvinceMapper.class,DistrictMapper.class,WardMapper.class}
)
public interface AddressMapper extends GennericMapper<Address, AddressRequest, AddressResponse> {
	@Override
	@Mapping(source = "provinceId", target = "province", qualifiedByName = "convertToProvince")
	@Mapping(source = "districtId", target = "district", qualifiedByName = "convertToDistrict")
	@Mapping(source = "wardId", target = "ward", qualifiedByName = "convertToWard")
	Address requestToEntity(AddressRequest request);


}
