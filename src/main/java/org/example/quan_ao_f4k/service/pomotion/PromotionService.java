package org.example.quan_ao_f4k.service.pomotion;

import org.example.quan_ao_f4k.dto.request.promotion.PromotionRequest;
import org.example.quan_ao_f4k.dto.response.promotion.PromotionResponse;
import org.example.quan_ao_f4k.model.promotion.Promotion;
import org.example.quan_ao_f4k.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;

public interface PromotionService extends CrudService<Long, PromotionRequest, PromotionResponse> {
    Page<PromotionResponse> searchPromotion(int page, int size, String search, Integer status, Integer effectiveDate);
    void addModelPromotionAdd(Model model);
    List<Promotion> getActivePromotions();

    BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, BigDecimal discountPercent);
    Promotion getBestPromotionForProduct(Long productId);
    Promotion getBestPromotionForProductDetail(Long productDetailId);
}
