package org.example.quan_ao_f4k.service.shop;

import org.example.quan_ao_f4k.dto.request.shop.ShopProductRequest;
import org.example.quan_ao_f4k.dto.response.shop.ShopProductResponse;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.model.order.ShippingInfo;
import org.example.quan_ao_f4k.model.promotion.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ShopProductService {
    void addModelFilter(Model model);
    Page<ShopProductResponse.ProductDetailDto> searchProducts(ShopProductRequest.RequestSearch requestSearch);
    void addModelProductDetail(Model model, String slug, String colorHex, String sizeName);

    void addModelHome(Model model);

    List<Promotion> getListPromotion();
    void addModelPromotion(Model model, Long idPromotion);

    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, BigDecimal discountPercent);
    Promotion getBestPromotionForProduct(Long productId);
    Promotion getBestPromotionForProductDetail(Long productDetailId);
}