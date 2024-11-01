package org.example.quan_ao_f4k.service.shop;

import org.example.quan_ao_f4k.dto.request.shop.ShopRequest;
import org.example.quan_ao_f4k.dto.response.shop.ShopResponse;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.model.order.Cart;
import org.example.quan_ao_f4k.model.order.CartProduct;
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
import org.example.quan_ao_f4k.util.SimpleEncoderDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

@Service
public class ShopProductService {

    @Autowired
    private CriteriaRepository criteriaRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartProductRepository cartProductRepository;

    @Autowired
    SizeRepository sizeRepository;
    @Autowired
    private UserRepository userRepository;

    public Page<ShopResponse.ProductListResponse> getListProductDetail(ShopRequest.RequestSearch requestSearch) {
        List<Product> productList = criteriaRepository.searchProductByRequest(requestSearch);

        List<ShopResponse.ProductListResponse> listData = new ArrayList<>();
        for (Product product : productList) {
            ProductDetail productDetail = criteriaRepository.getFirstProductDetailById(product.getId());
            if (productDetail == null) continue;

            ShopResponse.ProductListResponse objTmp = ShopResponse.ProductListResponse.builder()
                    .id(SimpleEncoderDecoder.encrypt(productDetail.getId() + ""))
                    .idParent(SimpleEncoderDecoder.encrypt(productDetail.getProduct().getId() + ""))
                    .color(productDetail.getColor())
                    .size(productDetail.getSize())
                    .price(productDetail.getPrice())
                    .quantity(productDetail.getQuantity())
                    .product(productDetail.getProduct())
                    .listImage(imageRepository.getImageByIdParent(productDetail.getId(), F4KConstants.TableCode.PRODUCT_DETAIL))
                    .build();
            listData.add(objTmp);
        }

        Pageable pageable = PageRequest.of(requestSearch.getPage(), requestSearch.getPageSize());
        return F4KUtils.toPage(listData, pageable);
    }

    public void addModelFilter(Model model) {
        model.addAttribute("listProduct", this.getProductCategory());
        model.addAttribute("listBrand", this.getProductBrand());
        model.addAttribute("listSize", criteriaRepository.findAllByStatus(Size.class));
        model.addAttribute("listColor", criteriaRepository.findAllByStatus(Color.class));
        model.addAttribute("listCategory", criteriaRepository.findAllByStatus(Category.class));
    }

    public void addModelProductDetail(Model model, String idParent, String colorName) {
        Long idProduct = Long.valueOf(SimpleEncoderDecoder.decrypt(idParent));

        ProductDetail objTmp = productDetailRepository
                .findProductDetailsByProductIdAndSizeAndColorId(idProduct, colorName)
                .orElse(new ProductDetail());

        List<Image> listImage = imageRepository.getImageByIdParent(objTmp.getId(), F4KConstants.TableCode.PRODUCT_DETAIL);
        List<Color> listColor = colorRepository.findByProductId(idProduct);

        List<Size> listSize = sizeRepository.findByProductIdAndColorName(idProduct, colorName);

        ShopRequest.RequestSearch requestSearch = ShopRequest.RequestSearch
                .builder()
                .brand(List.of(objTmp.getProduct().getBrand().getId() + ""))
                .page(0)
                .pageSize(8)
                .orderBy("asc")
                .build();

        model.addAttribute("objDetail", objTmp);
        model.addAttribute("listImage", listImage);
        model.addAttribute("listColor", listColor);
        model.addAttribute("listSize", listSize);
        model.addAttribute("listData", getListProductDetail(requestSearch));
    }

    public ShopResponse.CartResponse getListCart(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (Objects.isNull(user)) {
            return ShopResponse.CartResponse.builder()
                    .listData(new ArrayList<>())
                    .subtotal(BigDecimal.ZERO)
                    .build();
        }

        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
        if (Objects.isNull(cart)) {
            cart = cartRepository.save(
                    Cart.builder()
                            .user(user)
                            .status(F4KConstants.STATUS_ON)
                            .build()
            );
        }
        List<CartProduct> cartProductList = cartProductRepository.findAllByCart_Id(cart.getId());

        List<ShopResponse.CartDto> listCartDto = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartProduct item: cartProductList) {

            BigDecimal total = item.getProductDetail().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            item.getProductDetail().setIdEncore(SimpleEncoderDecoder.encrypt(item.getProductDetail().getId().toString()));
            subtotal = subtotal.add(total);

            ShopResponse.CartDto objTmp = ShopResponse.CartDto
                    .builder()
                    .image(imageRepository.findImageByIdParent(item.getProductDetail().getId(), F4KConstants.TableCode.PRODUCT_DETAIL))
                    .productDetail(item.getProductDetail())
                    .quantity(item.getQuantity())
                    .total(total)
                    .build();

            listCartDto.add(objTmp);
        }

        return ShopResponse.CartResponse.builder()
                .listData(listCartDto)
                .subtotal(subtotal)
                .build();
    }

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
