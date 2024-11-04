package org.example.quan_ao_f4k.service.shop;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.shop.ShopProductRequest;
import org.example.quan_ao_f4k.dto.response.shop.ShopProductResponse;
import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.mapper.shop.ShopProductMapper;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.model.order.Cart;
import org.example.quan_ao_f4k.model.product.*;
import org.example.quan_ao_f4k.repository.authentication.UserRepository;
import org.example.quan_ao_f4k.repository.general.ImageRepository;
import org.example.quan_ao_f4k.repository.order.CartProductRepository;
import org.example.quan_ao_f4k.repository.order.CartRepository;
import org.example.quan_ao_f4k.repository.product.ColorRepository;
import org.example.quan_ao_f4k.repository.product.ProductDetailRepository;
import org.example.quan_ao_f4k.repository.product.SizeRepository;
import org.example.quan_ao_f4k.repository.shop.CriteriaRepository;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.example.quan_ao_f4k.util.F4KUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class ShopProductServiceImpl implements ShopProductService {
    private final ProductDetailRepository productDetailRepository;
    private final ShopProductMapper shopProductMapper;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;

    @Autowired
    private CriteriaRepository criteriaRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartProductRepository cartProductRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<ShopProductResponse.ProductDetailDto> searchProducts(ShopProductRequest.RequestSearch requestSearch) {
        List<ProductDetail> productDetailList = productDetailRepository.getListSearch(
                requestSearch.getName()
                , requestSearch.getBrand()
                , requestSearch.getCategory()
                , requestSearch.getSize()
                , requestSearch.getColor()
                , requestSearch.getPriceForm()
                , requestSearch.getPriceTo()
                , requestSearch.getOrderBy()
        );
        List<ShopProductResponse.ProductDetailDto> listResponse = shopProductMapper.toProductDetailDto(productDetailList);

        Pageable pageable = PageRequest.of(requestSearch.getPage(), requestSearch.getPageSize());
        return F4KUtils.toPage(listResponse, pageable);
    }

    @Override
    public void addModelFilter(Model model) {
        model.addAttribute("listProduct", this.getProductCategory());
        model.addAttribute("listBrand", this.getProductBrand());
        model.addAttribute("listSize", criteriaRepository.findAllByStatus(Size.class));
        model.addAttribute("listColor", criteriaRepository.findAllByStatus(Color.class));
        model.addAttribute("listCategory", criteriaRepository.findAllByStatus(Category.class));
    }

    @Override
    public void addModelProductDetail(Model model, String slug, String colorHex, String sizeName) {
        ProductDetail productDetail = productDetailRepository.findProductDetailBySlugProduct(slug, colorHex, sizeName)
                .orElseThrow(() ->new BadRequestException("Lỗi không tìm thấy sản phẩm"));

        List<Image> listImage = imageRepository.getImageByIdParent(productDetail.getId(), F4KConstants.TableCode.PRODUCT_DETAIL);
        List<Color> listColor = colorRepository.findBySlugProduct(slug);
        List<Size> listSize = sizeRepository.findBySlugProduct(slug);

        ShopProductRequest.RequestSearch requestSearch = ShopProductRequest.RequestSearch
                .builder()
                .brand(List.of(productDetail.getProduct().getBrand().getId()))
                .page(0)
                .pageSize(8)
                .orderBy("asc")
                .build();

        model.addAttribute("objDetail", productDetail);
        model.addAttribute("listImage", listImage);
        model.addAttribute("listColor", listColor);
        model.addAttribute("listSize", listSize);
        model.addAttribute("listData", searchProducts(requestSearch));
    }

//    public ShopResponse.CartResponse getListCart(UserDetails userDetails) {
//        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
//
//        if (Objects.isNull(user)) {
//            return ShopResponse.CartResponse.builder()
//                    .listData(new ArrayList<>())
//                    .subtotal(BigDecimal.ZERO)
//                    .build();
//        }
//
//        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
//        if (Objects.isNull(cart)) {
//            cart = cartRepository.save(
//                    Cart.builder()
//                            .user(user)
//                            .status(F4KConstants.STATUS_ON)
//                            .build()
//            );
//        }
//        List<CartProduct> cartProductList = cartProductRepository.findAllByCart_Id(cart.getId());
//
//        List<ShopResponse.CartDto> listCartDto = new ArrayList<>();
//        BigDecimal subtotal = BigDecimal.ZERO;
//        for (CartProduct item: cartProductList) {
//
//            BigDecimal total = item.getProductDetail().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
//            item.getProductDetail().setIdEncore(SimpleEncoderDecoder.encrypt(item.getProductDetail().getId().toString()));
//            subtotal = subtotal.add(total);
//
//            ShopResponse.CartDto objTmp = ShopResponse.CartDto
//                    .builder()
//                    .image(imageRepository.findImageByIdParent(item.getProductDetail().getId(), F4KConstants.TableCode.PRODUCT_DETAIL))
//                    .productDetail(item.getProductDetail())
//                    .quantity(item.getQuantity())
//                    .total(total)
//                    .build();
//
//            listCartDto.add(objTmp);
//        }
//
//        return ShopResponse.CartResponse.builder()
//                .listData(listCartDto)
//                .subtotal(subtotal)
//                .build();
//    }

    public void addCart(Long id, User user) {

        if (Objects.isNull(user)) {
            return;
        }

        ProductDetail objTmp = productDetailRepository
                .findById(id)
                .orElse(null);

        Cart cart = Cart.builder()
                .user(user)
                .status(F4KConstants.STATUS_ON)
                .build();

        System.out.println(objTmp + " - " + cart);
    }

    private Map<Object, Number> getProductBrand() {
        return getProductMap(Brand.class,
                item -> criteriaRepository
                        .findProductByField(Product.class, "brand", item.getId()).size());
    }

    private Map<Object, Number> getProductCategory() {
        return getProductMap(Product.class,
                item -> criteriaRepository
                        .findProductByField(ProductDetail.class, "product", item.getId()).size());
    }

    private <T> Map<Object, Number> getProductMap(Class<T> clazz, Function<T, Integer> getSizeFunction) {
        List<T> items = criteriaRepository.findAllByStatus(clazz);
        Map<Object, Number> category = new HashMap<>();
        for (T item : items) {
            int size = getSizeFunction.apply(item);
            if (size <= 0) continue;
            category.put(item, size);
        }
        return category;
    }

}
