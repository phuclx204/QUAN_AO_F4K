package org.example.quan_ao_f4k.dto.response.promotion;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PromotionProductResponse {

    private Long id;
    private String nameProduct;
    private Integer quantity;
    private BigDecimal discountValue;
    private int status;
    private int type;
}
