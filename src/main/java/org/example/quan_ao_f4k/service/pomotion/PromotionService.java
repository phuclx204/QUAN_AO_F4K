package org.example.quan_ao_f4k.service.pomotion;

import org.example.quan_ao_f4k.dto.request.promotion.PromotionRequest;
import org.example.quan_ao_f4k.dto.response.promotion.PromotionResponse;
import org.example.quan_ao_f4k.service.CrudService;
import org.springframework.data.domain.Page;

public interface PromotionService extends CrudService<Long, PromotionRequest, PromotionResponse> {
    Page<PromotionResponse> searchPromotion(int page, int size, String search, Integer status, Integer effectiveDate);
}
