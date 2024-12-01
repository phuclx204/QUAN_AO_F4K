package org.example.quan_ao_f4k.dto.response.promotion;

import lombok.Builder;
import lombok.Data;
import org.example.quan_ao_f4k.dto.request.promotion.PromotionProductRequest;
import org.example.quan_ao_f4k.model.promotion.PromotionProduct;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class PromotionResponse {
    private Long id;
    private String name;
    private LocalDate dayStart;
    private LocalDate dayEnd;
    private int status;
    private BigDecimal discountValue;
    private List<PromotionProductResponse> products;
}
