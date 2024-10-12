package org.example.quan_ao_f4k.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BrandResponse {
    private Long id;
    private String name;
    private Integer status;
}
