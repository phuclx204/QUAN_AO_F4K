package org.example.quan_ao_f4k.service.shop;

import org.example.quan_ao_f4k.dto.request.shop.ShopRequest;
import org.example.quan_ao_f4k.dto.response.shop.ShopResponse;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.model.order.Cart;
import org.example.quan_ao_f4k.model.product.*;
import org.example.quan_ao_f4k.repository.general.ImageRepository;
import org.example.quan_ao_f4k.repository.order.CartProductRepository;
import org.example.quan_ao_f4k.repository.order.CartRepository;
import org.example.quan_ao_f4k.repository.product.ColorRepository;
import org.example.quan_ao_f4k.repository.product.ProductDetailRepository;
import org.example.quan_ao_f4k.repository.product.ProductRepository;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public Page<ShopResponse.ProductListResponse> getListProductDetail(ShopRequest.RequestSearch requestSearch) {
        List<Product> productList = criteriaRepository.searchProductByRequest(requestSearch);

        List<ShopResponse.ProductListResponse> listData = new ArrayList<>();
        for (Product product : productList) {
            ProductDetail productDetail = criteriaRepository.getFirstProductDetailById(product.getId());
            if (productDetail == null) continue;

            ShopResponse.ProductListResponse objTmp = ShopResponse.ProductListResponse.builder()
                    .id(SimpleEncoderDecoder.encode(productDetail.getId() + ""))
                    .idParent(SimpleEncoderDecoder.encode(productDetail.getProduct().getId() + ""))
                    .color(productDetail.getColor())
                    .size(productDetail.getSize())
                    .price(productDetail.getPrice())
                    .quantity(productDetail.getQuantity())
                    .product(productDetail.getProduct())
                    .listImage(imageRepository.getImageByIdParent(productDetail.getId()))
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
        Long idProduct = Long.valueOf(SimpleEncoderDecoder.decode(idParent));

        ProductDetail objTmp = productDetailRepository
                .findProductDetailsByProductIdAndSizeAndColorId(idProduct, colorName)
                .orElse(new ProductDetail());

        List<Image> listImage = imageRepository.getImageByIdParent(objTmp.getId());
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

    public void addCart(Long id, User user) {

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
