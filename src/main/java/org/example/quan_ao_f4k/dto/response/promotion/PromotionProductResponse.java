package org.example.quan_ao_f4k.dto.response.promotion;

import lombok.Builder;
import lombok.Data;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.model.product.ProductDetail;

import java.math.BigDecimal;

@Data
@Builder
public class PromotionProductResponse {
    private Long id;
    private ProductDetail productDetail;
    private Integer quantity;
    private BigDecimal discountValue;
    private int status;
    private int type;
}
