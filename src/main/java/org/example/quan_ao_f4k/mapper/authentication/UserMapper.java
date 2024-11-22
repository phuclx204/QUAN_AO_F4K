package org.example.quan_ao_f4k.mapper.authentication;

import org.example.quan_ao_f4k.dto.request.authentication.RegisterRequest;
import org.example.quan_ao_f4k.dto.request.authentication.UserRequest;
import org.example.quan_ao_f4k.dto.response.authentication.UserDto;
import org.example.quan_ao_f4k.dto.response.authentication.UserResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.mapper.MapperCoverter;
import org.example.quan_ao_f4k.mapper.address.AddressMapper;
import org.example.quan_ao_f4k.model.authentication.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE
		,uses = {MapperCoverter.class, AddressMapper.class,RoleMapper.class})
public interface UserMapper extends GennericMapper<User, UserRequest, UserDto> {
	@Override
	@Mapping(source = "addressId", target = "address", qualifiedByName = "convertToAddress")
	@Mapping(source = "roleId", target = "role", qualifiedByName = "convertToRole")
	User requestToEntity(UserRequest request);

	@Override
	@Mapping(source = "addressId", target = "address", qualifiedByName = "convertToAddress")
	@Mapping(source = "roleId", target = "role", qualifiedByName = "convertToRole")
	User partialUpdate(@MappingTarget User user, UserRequest request);
}
