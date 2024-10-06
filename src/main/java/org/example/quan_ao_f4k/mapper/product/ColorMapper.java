package org.example.quan_ao_f4k.mapper.product;

import org.example.quan_ao_f4k.dto.request.product.ColorRequest;
import org.example.quan_ao_f4k.dto.response.product.ColorResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.model.product.Color;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ColorMapper extends GennericMapper<Color, ColorRequest, ColorResponse> {
}
