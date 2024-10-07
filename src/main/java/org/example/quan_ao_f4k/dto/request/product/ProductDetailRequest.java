package org.example.quan_ao_f4k.dto.request.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDetailRequest {
    private Long productId;
    private Long sizeId;
    private Long colorId;
    private BigDecimal price;
    private Long guaranteeId;
    private Integer quantity;
    private String thumbnail;
    private Integer status;
}
