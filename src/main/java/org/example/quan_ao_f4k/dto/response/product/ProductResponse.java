package org.example.quan_ao_f4k.dto.response.product;

import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private CategoryResponse category;
    private BrandResponse brand;
    private String thumbnail;
    private String description;
    private Integer status;
    @Data
    public static class BrandResponse {
        private Long id;
        private String name;

    }

    @Data
    public static class CategoryResponse {
        private Long id;
        private String name;
        private String description;
    }
}


