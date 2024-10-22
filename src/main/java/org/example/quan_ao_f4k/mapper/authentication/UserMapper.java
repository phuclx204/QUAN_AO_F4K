package org.example.quan_ao_f4k.mapper.authentication;

import org.example.quan_ao_f4k.dto.request.authentication.RegisterRequest;
import org.example.quan_ao_f4k.dto.response.authentication.UserDto;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.model.authentication.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends GennericMapper<User, RegisterRequest, UserDto> {
}
