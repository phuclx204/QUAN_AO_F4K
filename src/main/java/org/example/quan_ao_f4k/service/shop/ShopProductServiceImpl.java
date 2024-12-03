package org.example.quan_ao_f4k.service.shop;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.quan_ao_f4k.dto.request.shop.ShopProductRequest;
import org.example.quan_ao_f4k.dto.response.shop.ShopProductResponse;
import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.mapper.shop.ShopProductMapper;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.model.product.*;
import org.example.quan_ao_f4k.model.promotion.Promotion;
import org.example.quan_ao_f4k.model.promotion.PromotionProduct;
import org.example.quan_ao_f4k.repository.general.ImageRepository;
import org.example.quan_ao_f4k.repository.product.ColorRepository;
import org.example.quan_ao_f4k.repository.product.ProductDetailRepository;
import org.example.quan_ao_f4k.repository.product.ProductRepository;
import org.example.quan_ao_f4k.repository.product.SizeRepository;
import org.example.quan_ao_f4k.repository.promotion.PromotionProductRepository;
import org.example.quan_ao_f4k.repository.promotion.PromotionRepository;
import org.example.quan_ao_f4k.repository.shop.CriteriaRepository;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.example.quan_ao_f4k.util.F4KUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
public class ShopProductServiceImpl implements ShopProductService {
    private final ProductDetailRepository productDetailRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;
    private final ImageRepository imageRepository;
    private final CriteriaRepository criteriaRepository;
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    private final PromotionProductRepository promotionProductRepository;

    private final ShopProductMapper shopProductMapper;

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
        applyDiscounts(listResponse);

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
                .orElseThrow(() -> new BadRequestException("Lỗi không tìm thấy sản phẩm"));

        List<ProductDetail> productDetailList = productDetailRepository.findProductDetailBySlugProduct(slug, productDetail.getColor().getHex());

        List<Size> listSize = productDetailList.stream().map(el -> {
            if (el.getStatus() == F4KConstants.STATUS_OFF) el.getSize().setStatus(F4KConstants.STATUS_OFF);
            else el.getSize().setStatus(F4KConstants.STATUS_ON);
            return el.getSize();
        }).toList();

        List<Color> listColor = colorRepository.findBySlugProduct(slug);

//        List<Size> listSize = sizeRepository.findBySlugProduct(slug, productDetail.getColor().getHex());

        List<Image> listImage = imageRepository.getImageByIdParent(productDetail.getProduct().getId(), F4KConstants.TableCode.PRODUCT_DETAIL);

        ShopProductRequest.RequestSearch requestSearch = ShopProductRequest.RequestSearch
                .builder()
                .brand(List.of(productDetail.getProduct().getBrand().getId()))
                .page(0)
                .pageSize(8)
                .orderBy("asc")
                .build();

        Promotion promotion = getBestPromotionForProductDetail(productDetail.getId());
        BigDecimal finalDiscount = BigDecimal.ZERO;
        if (promotion != null) {
            finalDiscount = calculateDiscountedPrice(productDetail.getPrice(), promotion.getDiscountValue());
        }

        model.addAttribute("objDetail", productDetail);
        model.addAttribute("listImage", listImage);
        model.addAttribute("listColor", listColor);
        model.addAttribute("listSize", listSize);
        model.addAttribute("listData", searchProducts(requestSearch));
        model.addAttribute("promotion", promotion);
        model.addAttribute("finalDiscount", finalDiscount);
    }

    @Override
    public void addModelHome(Model model) {
        ShopProductRequest.RequestSearch requestSearch = ShopProductRequest.RequestSearch
                .builder()
                .page(0)
                .pageSize(8)
                .orderBy("asc")
                .build();

        ShopProductRequest.RequestSearch requestSearch2 = ShopProductRequest.RequestSearch
                .builder()
                .page(0)
                .pageSize(3)
                .orderBy("desc")
                .build();



        List<ShopProductResponse.ProductDetailDto> listProduct2 = searchProducts(requestSearch2).getContent();
        Map<Integer, List<ShopProductResponse.ProductDetailDto>> mapProduct = new HashMap<>();

        Integer index = 0;
        for (ShopProductResponse.ProductDetailDto item: listProduct2) {
            ShopProductRequest.RequestSearch requestSearch3 = ShopProductRequest.RequestSearch
                    .builder()
                    .brand(List.of(item.getProduct().getBrand().getId()))
                    .page(0)
                    .pageSize(4)
                    .orderBy("desc")
                    .build();
            List<ShopProductResponse.ProductDetailDto> listProductTmp = searchProducts(requestSearch3).getContent();
            mapProduct.put(index, listProductTmp);
            index += 1;
        }



        model.addAttribute("listProduct", searchProducts(requestSearch));
        model.addAttribute("listProduct2", listProduct2);
        model.addAttribute("mapProduct", mapProduct);
    }

    @Override
    public List<Promotion> getListPromotion() {
        return promotionRepository.findAllByStatusAndDayStartBeforeAndDayEndAfter(F4KConstants.STATUS_ON, LocalDate.now());
    }

    private List<ShopProductResponse.ProductDetailDto> getListProductByPromotion(Long promotionId) {
        List<ProductDetail> productDetailList = productDetailRepository.getListByPromotionId(promotionId);
        List<ShopProductResponse.ProductDetailDto> listResponse = shopProductMapper.toProductDetailDto(productDetailList);
        applyDiscounts(listResponse);
        return listResponse;
    }

    @Override
    public void addModelPromotion(Model model, Long idPromotion) {
        List<PromotionProduct> promotionProducts = promotionProductRepository.findByPromotionId(idPromotion, null);

        for (PromotionProduct promotionProduct: promotionProducts) {
            Long idProduct = promotionProduct.getProduct().getId();

            Promotion promotion = getBestPromotionForProduct(idProduct);
            if (promotion == null ) {
                continue;
            }

            if (promotion.getId() != idPromotion) {
                promotionProduct.setStatus(F4KConstants.STATUS_OFF);
            } else {
                promotionProduct.setStatus(F4KConstants.STATUS_ON);
            }
        }
        promotionProductRepository.saveAll(promotionProducts);

        List<ShopProductResponse.ProductDetailDto> listProducts = getListProductByPromotion(idPromotion);
        model.addAttribute("listPromotion", listProducts);
    }

    @Override
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, BigDecimal discountPercent) {
        if (originalPrice == null || discountPercent == null) {
            return originalPrice;
        }

        if (discountPercent.compareTo(BigDecimal.ZERO) < 0 || discountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Phần trăm giảm giá phải nằm trong khoảng từ 0 đến 100.");
        }

        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                discountPercent.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)
        );

        return originalPrice.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Promotion getBestPromotionForProduct(Long productId) {
        LocalDate now = LocalDate.now();
        List<Promotion> promotions = promotionRepository.findActivePromotionsByProductId(productId, now);
        return promotions.isEmpty() ? null : promotions.get(0);
    }

    @Override
    public Promotion getBestPromotionForProductDetail(Long productDetailId) {
        LocalDate now = LocalDate.now();
        List<Promotion> promotions = promotionRepository.findActivePromotionsByProductDetailId(productDetailId, now);
        return promotions.isEmpty() ? null : promotions.get(0);
    }

    private void applyDiscounts(List<ShopProductResponse.ProductDetailDto> listResponse) {
        listResponse.forEach(el -> {
            var promotion = getBestPromotionForProductDetail(el.getId());
            if (promotion != null) {
                BigDecimal finalPrice = calculateDiscountedPrice(el.getPrice(), promotion.getDiscountValue());
                if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
                    finalPrice = BigDecimal.ZERO;
                }
                el.setDiscountValue(finalPrice);
                el.setPromotion(promotion);
            } else {
                el.setDiscountValue(el.getPrice());
                el.setPromotion(null);
            }
        });
    }

    private void getImagesProductDetail(List<Image> listImage, String slug, String colorHex) {
        List<ProductDetail> productDetailList = productDetailRepository.findProductDetailBySlugProduct(slug, colorHex);
        productDetailList.forEach(el -> listImage.addAll(imageRepository.getImageByIdParent(el.getId(), F4KConstants.TableCode.PRODUCT_DETAIL)));
    }

    private Map<Object, Number> getProductBrand() {
        return getProductMap(Brand.class,
                item -> productRepository
                        .findProductByBrandId(item.getId()).size());
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
