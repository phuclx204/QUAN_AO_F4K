package org.example.quan_ao_f4k.mapper.users;

import org.example.quan_ao_f4k.dto.request.users.CustomerRequest;
import org.example.quan_ao_f4k.dto.response.users.CustomerResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.mapper.MapperCoverter;
import org.example.quan_ao_f4k.mapper.address.AddressMapper;
import org.example.quan_ao_f4k.mapper.authentication.RoleMapper;
import org.example.quan_ao_f4k.model.authentication.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE
		,uses = {MapperCoverter.class, AddressMapper.class, RoleMapper.class})
public interface CustomerMapper extends GennericMapper<User, CustomerRequest, CustomerResponse> {
	@Override
	@Mapping(source = "addressId", target = "address", qualifiedByName = "convertToAddress")
	@Mapping(source = "roleId", target = "role", qualifiedByName = "convertToRole")
	User requestToEntity(CustomerRequest request);

	@Override
	@Mapping(source = "addressId", target = "address", qualifiedByName = "convertToAddress")
	@Mapping(source = "roleId", target = "role", qualifiedByName = "convertToRole")
	User partialUpdate(@MappingTarget User user, CustomerRequest request);
}
