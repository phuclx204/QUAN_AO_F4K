package org.example.quan_ao_f4k.mapper.shop;

import org.example.quan_ao_f4k.dto.response.shop.ShopProductResponse;
import org.example.quan_ao_f4k.mapper.MapperCoverter;
import org.example.quan_ao_f4k.mapper.product.BrandMapper;
import org.example.quan_ao_f4k.mapper.product.CategoryMapper;
import org.example.quan_ao_f4k.model.order.CartProduct;
import org.example.quan_ao_f4k.model.order.ShippingInfo;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {MapperCoverter.class, CategoryMapper.class, BrandMapper.class})
public interface ShopProductMapper {

    ShopProductResponse.ProductDetailDto toProductDetailDto(ProductDetail productDetail);
    ProductDetail toProductDetail(ShopProductResponse.ProductDetailDto productDetailDto);

    List<ShopProductResponse.ProductDetailDto> toProductDetailDto(List<ProductDetail> list);

    @Mapping(source = "id", target = "image", qualifiedByName = "convertToImageByProduct")
    @Mapping(source = "id", target = "images", qualifiedByName = "convertToImageByProductDetail")
    @Mapping(source = "slug", target = "slug")
    ShopProductResponse.ProductDto toProductDto(Product product);
    Product toProduct(ShopProductResponse.ProductDto productDto);

    // for cart
    @Mapping(source = "productDetail", target = "productDetailDto")
    ShopProductResponse.CartProductDto toCartProductDto(CartProduct cartProduct);
    CartProduct toCartProduct(ShopProductResponse.CartProductDto cartProductDto);

    List<ShopProductResponse.CartProductDto> toCartProductDto(List<CartProduct> list);

    ShopProductResponse.ShippingInfoDto toShippingInfoDto(ShippingInfo shippingInfo);
    ShippingInfo toShippingInfo(ShopProductResponse.ShippingInfoDto productDto);
    List<ShopProductResponse.ShippingInfoDto> toShippingInfoDto(List<ShippingInfo> list);
}
