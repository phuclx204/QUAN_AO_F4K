package org.example.quan_ao_f4k.mapper;

import org.example.quan_ao_f4k.model.address.Address;
import org.example.quan_ao_f4k.model.address.District;
import org.example.quan_ao_f4k.model.address.Province;
import org.example.quan_ao_f4k.model.address.Ward;
import org.example.quan_ao_f4k.model.authentication.Role;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.model.order.Order;
import org.example.quan_ao_f4k.model.order.PaymentMethod;
import org.example.quan_ao_f4k.model.product.*;
import org.example.quan_ao_f4k.model.promotion.Promotion;
import org.example.quan_ao_f4k.repository.address.AddressRepository;
import org.example.quan_ao_f4k.repository.address.DistrictRepository;
import org.example.quan_ao_f4k.repository.address.ProvinceRepository;
import org.example.quan_ao_f4k.repository.address.WardRepository;
import org.example.quan_ao_f4k.repository.authentication.RoleRepository;
import org.example.quan_ao_f4k.repository.authentication.UserRepository;
import org.example.quan_ao_f4k.repository.order.OrderRepository;
import org.example.quan_ao_f4k.repository.order.PaymentMethodRepository;
import org.example.quan_ao_f4k.repository.product.*;
import org.example.quan_ao_f4k.repository.promotion.PromotionRepository;
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
    private SizeRepository sizeRepository;
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
	@Autowired
	private OrderRepository orderRepository;
    @Autowired
    private PromotionRepository promotionRepository;

    @Named("convertToBrand")
    public Brand convertToBrand(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi tìm Brand"));
    }

    @Named("convertToCategory")
    public Category convertToCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi tìm Category"));
    }

    @Named("convertToProductDetail")
    public ProductDetail convertToProductDetail(Long id) {
        return productDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi tìm Product Detail"));
    }

    @Named("convertToSize")
    public Size convertToSize(Long id) {
        return sizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi tìm Size"));
    }

    @Named("convertToColor")
    public Color convertToColor(Long id) {
        return colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi tìm Color"));
    }

    @Named("convertToProduct")
    public Product convertToProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi tìm Product"));
    }

    @Named("convertToAddress")
    public Address convertToAddress(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi tìm Address"));
    }

    @Named("convertToProvince")
    public Province convertToProvince(Long id) {
        return provinceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi tìm Province"));
    }

    @Named("convertToDistrict")
    public District convertToDistrict(Long id) {
        return districtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi tìm District"));
    }

    @Named("convertToWard")
    public Ward convertToWard(Long id) {
        return wardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi tìm Ward"));
    }

    @Named("convertToUser")
    public User convertToUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi tìm User"));
    }

    @Named("convertToRole")
    public Role convertToRole(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi tìm Role"));
    }

    @Named("convertToPayment")
    public PaymentMethod convertToPayment(Long id) {
        return paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi tìm Payment Method"));
    }

    @Named("convertToOrder")
    public Order convertToOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Loi tim order"));
    }

    @Named("convertToPromotion")
    public Promotion convertToPromotion(Long id) {
        return promotionRepository.findById(id).orElseThrow(() -> new RuntimeException("Lỗi tìm promotion"));
    }

}
