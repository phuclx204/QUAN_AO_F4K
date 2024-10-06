package org.example.quan_ao_f4k.mapper;

import org.example.quan_ao_f4k.model.product.Brand;
import org.example.quan_ao_f4k.model.product.Category;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.example.quan_ao_f4k.repository.product.BrandRepository;
import org.example.quan_ao_f4k.repository.product.CategoryRepository;
import org.example.quan_ao_f4k.repository.product.ProductDetailRepository;
import org.example.quan_ao_f4k.repository.product.ProductRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MapperCoverter {

    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Named("convertToBrand")
    public Brand convertToBrand(Long id){
        return brandRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Loi tim brand"));
    }
    @Named("convertToCategory")
    public Category convertToCategory(Long id){
        return categoryRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Loi tim cate"));
    }
    @Named("convertToProductDetail")
    public ProductDetail convertToProductDetail(Long id){
        return productDetailRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Loi tim cate"));
    }

}
