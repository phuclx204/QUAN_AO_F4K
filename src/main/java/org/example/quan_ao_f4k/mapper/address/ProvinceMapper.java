package org.example.quan_ao_f4k.mapper.address;

import org.example.quan_ao_f4k.dto.request.address.ProvinceRequest;
import org.example.quan_ao_f4k.dto.response.address.ProvinceResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.model.address.Province;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProvinceMapper extends GennericMapper<Province, ProvinceRequest, ProvinceResponse> {
}
