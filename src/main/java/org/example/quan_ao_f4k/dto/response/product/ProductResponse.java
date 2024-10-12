package org.example.quan_ao_f4k.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.quan_ao_f4k.model.product.Brand;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductResponse {
    private Long id;
    private String name;
    private CategoryResponse category;
    private Brand brand;
    private String thumbnail;
    private String description;
    private Integer status;
}


