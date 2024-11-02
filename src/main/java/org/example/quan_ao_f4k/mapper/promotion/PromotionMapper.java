package org.example.quan_ao_f4k.mapper.promotion;

import org.example.quan_ao_f4k.dto.request.promotion.PromotionRequest;
import org.example.quan_ao_f4k.dto.response.promotion.PromotionResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.model.promotion.Promotion;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PromotionMapper extends GennericMapper<Promotion, PromotionRequest, PromotionResponse> {
}
