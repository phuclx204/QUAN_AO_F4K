package org.example.quan_ao_f4k.dto.request.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDetailRequest {

    private Long colorId;
    private Long productId;
    private Long guaranteeId;
    private Long sizeId;
    private BigDecimal price;
    private Integer quantity;
}
