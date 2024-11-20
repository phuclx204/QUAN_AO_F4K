package org.example.quan_ao_f4k.mapper.product;

import org.example.quan_ao_f4k.dto.request.product.ProductRequest;
import org.example.quan_ao_f4k.dto.response.product.ProductResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.mapper.MapperCoverter;
import org.example.quan_ao_f4k.model.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {MapperCoverter.class, CategoryMapper.class, BrandMapper.class})
public interface ProductMapper extends GennericMapper<Product, ProductRequest, ProductResponse> {

    @Override
    @Mapping(source = "categoryId", target = "category", qualifiedByName = "convertToCategory")
    @Mapping(source = "brandId", target = "brand", qualifiedByName = "convertToBrand")
    @Mapping(source = "thumbnailName", target = "thumbnail")
    @Mapping(source = "slug", target = "slug")
    Product requestToEntity(ProductRequest request);

    @Override
    @Mapping(source = "categoryId", target = "category", qualifiedByName = "convertToCategory")
    @Mapping(source = "brandId", target = "brand", qualifiedByName = "convertToBrand")
    @Mapping(source = "thumbnailName", target = "thumbnail")
    Product partialUpdate(@MappingTarget Product entity, ProductRequest request);

    @Override
    @Mapping(source = "id", target = "image", qualifiedByName = "convertToImageByProduct")
    ProductResponse entityToResponse(Product product);
}
