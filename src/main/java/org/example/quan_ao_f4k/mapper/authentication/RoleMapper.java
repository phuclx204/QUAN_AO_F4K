package org.example.quan_ao_f4k.mapper.authentication;

import org.example.quan_ao_f4k.dto.request.authentication.RoleRequest;
import org.example.quan_ao_f4k.dto.response.authentication.RoleResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.model.authentication.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper extends GennericMapper<Role, RoleRequest, RoleResponse> {

}
