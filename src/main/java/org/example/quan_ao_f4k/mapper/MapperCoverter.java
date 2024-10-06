package org.example.quan_ao_f4k.mapper;

import org.example.quan_ao_f4k.model.product.*;
import org.example.quan_ao_f4k.repository.product.*;
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

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private GuaranteeRepository guaranteeRepository;

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

    @Named("convertToSize")
    public Size convertToSize(Long id){
        return sizeRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Loi tim cate"));
    }

    @Named("convertToColor")
    public Color convertToColor(Long id){
        return colorRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Loi tim cate"));
    }

    @Named("convertToProduct")
    public Product convertToProduct(Long id){
        return productRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Loi tim cate"));
    }

    @Named("convertToGuarantee")
    public Guarantee convertToGuarantee(Long id){
        return guaranteeRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Loi tim cate"));
    }

}
