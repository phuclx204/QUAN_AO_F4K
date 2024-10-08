package org.example.quan_ao_f4k.dto.request.product;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private Long categoryId;
    private Long brandId;
    private String thumbnail;
    private String description;
    private Integer status;
}
