package org.example.quan_ao_f4k.mapper.users;

import org.example.quan_ao_f4k.dto.request.users.EmployeeRequest;
import org.example.quan_ao_f4k.dto.response.users.EmployeeResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.mapper.MapperCoverter;
import org.example.quan_ao_f4k.model.authentication.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE
		, uses = {MapperCoverter.class, CustomerMapper.class})
public interface EmployeeMapper extends GennericMapper<Employee, EmployeeRequest, EmployeeResponse> {
	@Override
	@Mapping(source = "userId", target = "user", qualifiedByName = "convertToUser")
	Employee requestToEntity(EmployeeRequest request);

	@Override
	@Mapping(source = "userId", target = "user", qualifiedByName = "convertToUser")
	Employee partialUpdate(@MappingTarget Employee employee, EmployeeRequest request);
}
