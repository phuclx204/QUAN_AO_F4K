package org.example.quan_ao_f4k.mapper.shop;

import org.example.quan_ao_f4k.dto.response.shop.ShopProductResponse;
import org.example.quan_ao_f4k.mapper.MapperCoverter;
import org.example.quan_ao_f4k.mapper.product.BrandMapper;
import org.example.quan_ao_f4k.mapper.product.CategoryMapper;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {MapperCoverter.class, CategoryMapper.class, BrandMapper.class})
public interface ShopProductMapper {

    @Mapping(source = "id", target = "images", qualifiedByName = "convertToImageByProductDetail")
    ShopProductResponse.ProductDetailDto toProductDetailDto(ProductDetail productDetail);
    ProductDetail toProductDetail(ShopProductResponse.ProductDetailDto productDetailDto);

    List<ShopProductResponse.ProductDetailDto> toProductDetailDto(List<ProductDetail> list);

    ShopProductResponse.ProductDto toProductDto(Product product);
    Product toProduct(ShopProductResponse.ProductDto productDto);
}
