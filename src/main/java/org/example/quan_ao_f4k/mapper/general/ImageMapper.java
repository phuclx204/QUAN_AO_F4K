package org.example.quan_ao_f4k.mapper.general;

import org.example.quan_ao_f4k.dto.request.general.ImageRequest;
import org.example.quan_ao_f4k.dto.response.general.ImageResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.model.general.Image;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ImageMapper extends GennericMapper<Image, ImageRequest, ImageResponse> {
}
