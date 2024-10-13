package org.example.quan_ao_f4k.dto.request.product;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductRequest {
    private String name;
    private Long categoryId;
    private Long brandId;
    private MultipartFile thumbnail;
    private String thumbnailName;
    private String description;
    private Integer status = 1;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
