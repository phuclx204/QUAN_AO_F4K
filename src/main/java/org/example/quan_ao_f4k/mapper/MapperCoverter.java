package org.example.quan_ao_f4k.mapper;

import org.example.quan_ao_f4k.model.address.Address;
import org.example.quan_ao_f4k.model.address.District;
import org.example.quan_ao_f4k.model.address.Province;
import org.example.quan_ao_f4k.model.address.Ward;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.model.product.Brand;
import org.example.quan_ao_f4k.model.product.Category;
import org.example.quan_ao_f4k.repository.address.AddressRepository;
import org.example.quan_ao_f4k.repository.address.DistrictRepository;
import org.example.quan_ao_f4k.repository.address.ProvinceRepository;
import org.example.quan_ao_f4k.repository.address.WardRepository;
import org.example.quan_ao_f4k.repository.authentication.UserRepository;
import org.example.quan_ao_f4k.repository.product.BrandRepository;
import org.example.quan_ao_f4k.repository.product.CategoryRepository;
import org.example.quan_ao_f4k.repository.product.ProductDetailRepository;
import org.example.quan_ao_f4k.repository.product.ProductRepository;
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
    private AddressRepository addressRepository;
    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private WardRepository wardRepository;
    @Autowired
    private UserRepository userRepository;


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
    public ProductDetail convertToProductDetail(Long id) {
        return productDetailRepository.findById(id).orElseThrow(() -> new RuntimeException("Loi tim product detail"));
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
    @Named("convertToAddress")
    public Address convertToAddress(Long id){
        return addressRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Loi tim ..."));
    }

    @Named("convertToProvince")
    public Province convertToProvince(Long id){
        return provinceRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Loi tim province"));
    }
    @Named("convertToUser")
    public User convertToUser(Long id){
        return userRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Loi tim user"));
    }

    @Named("convertToDistrict")
    public District convertToDistrict(Long id){
        return districtRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Loi tim District"));
    }

    @Named("convertToWard")
    public Ward convertToWard(Long id){
        return wardRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Loi tim Ward"));
    }


}
