package org.example.quan_ao_f4k.dto.response.product;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BrandResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
