package org.example.quan_ao_f4k.dto.response.product;

import lombok.Data;
import org.example.quan_ao_f4k.dto.response.general.ImageResponse;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.model.product.Color;
import org.example.quan_ao_f4k.model.product.Guarantee;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.model.product.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class ProductDetailResponse {
    private Long id;
    private ProductResponse product;
    private SizeResponse size;
    private ColorResponse color;
    private BigDecimal price;
    private Integer quantity;
    private Integer status;
    private LocalDateTime createdAt;
    private List<Image> images;
}
