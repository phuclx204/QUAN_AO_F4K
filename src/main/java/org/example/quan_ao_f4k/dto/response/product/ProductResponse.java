package org.example.quan_ao_f4k.dto.response.product;

import lombok.*;
import org.example.quan_ao_f4k.model.product.Brand;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private CategoryResponse category;
    private BrandResponse brand;
    private String thumbnail;
    private String description;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String pathImg;
    private String slug;
}


