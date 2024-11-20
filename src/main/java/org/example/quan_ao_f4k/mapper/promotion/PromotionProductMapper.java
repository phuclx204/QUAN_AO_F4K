package org.example.quan_ao_f4k.mapper.promotion;

import org.example.quan_ao_f4k.dto.request.promotion.PromotionProductRequest;
import org.example.quan_ao_f4k.dto.response.promotion.PromotionProductResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.mapper.MapperCoverter;
import org.example.quan_ao_f4k.mapper.product.ProductMapper;
import org.example.quan_ao_f4k.model.promotion.PromotionProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE
        , uses = {MapperCoverter.class, ProductMapper.class, PromotionMapper.class}
)
public interface PromotionProductMapper extends GennericMapper<PromotionProduct, PromotionProductRequest, PromotionProductResponse> {

    @Override
    @Mapping(source = "promotionId", target = "promotion", qualifiedByName = "convertToPromotion")
    @Mapping(source = "productId", target = "product", qualifiedByName = "convertToProduct")
    PromotionProduct requestToEntity(PromotionProductRequest request);

    @Override
    @Mapping(source = "promotionId", target = "promotion", qualifiedByName = "convertToPromotion")
    @Mapping(source = "productId", target = "product", qualifiedByName = "convertToProduct")
    PromotionProduct partialUpdate(@MappingTarget PromotionProduct promotionProduct, PromotionProductRequest request);
}
