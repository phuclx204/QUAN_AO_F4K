package org.example.quan_ao_f4k.mapper.product;

import org.example.quan_ao_f4k.dto.request.product.ProductDetailRequest;
import org.example.quan_ao_f4k.dto.response.product.ProductDetailResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.mapper.MapperCoverter;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring"
        , unmappedTargetPolicy = ReportingPolicy.IGNORE
        , uses = {MapperCoverter.class, SizeMapper.class, ProductMapper.class, GuaranteeMapper.class, ColorMapper.class})
public interface ProductDetailMapper extends GennericMapper<ProductDetail, ProductDetailRequest, ProductDetailResponse> {

    @Override
    @Mapping(source = "colorId", target = "color", qualifiedByName = "convertToColor")
    @Mapping(source = "productId", target = "product", qualifiedByName = "convertToProduct")
    @Mapping(source = "sizeId", target = "size", qualifiedByName = "convertToSize")
    @Mapping(source = "guaranteeId", target = "guarantee", qualifiedByName = "convertToGuarantee")
    ProductDetail requestToEntity(ProductDetailRequest request);

    @Override
    @Mapping(source = "colorId", target = "color", qualifiedByName = "convertToColor")
    @Mapping(source = "productId", target = "product", qualifiedByName = "convertToProduct")
    @Mapping(source = "sizeId", target = "size", qualifiedByName = "convertToSize")
    @Mapping(source = "guaranteeId", target = "guarantee", qualifiedByName = "convertToGuarantee")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    ProductDetail partialUpdate(@MappingTarget ProductDetail productDetail, ProductDetailRequest request);

}
