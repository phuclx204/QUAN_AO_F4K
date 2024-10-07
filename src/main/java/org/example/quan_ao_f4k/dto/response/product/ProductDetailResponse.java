package org.example.quan_ao_f4k.dto.response.product;

import lombok.Data;
import org.example.quan_ao_f4k.model.product.Color;
import org.example.quan_ao_f4k.model.product.Guarantee;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.model.product.Size;

import java.math.BigDecimal;

@Data
public class ProductDetailResponse {
    private Product product;
    private Size size;
    private Color color;
    private BigDecimal price;
    private Guarantee guarantee;
    private Integer quantity;
    private String thumbnail;
    private Integer status;
}
