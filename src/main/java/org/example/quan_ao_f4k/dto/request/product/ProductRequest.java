package org.example.quan_ao_f4k.dto.request.product;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductRequest implements Serializable {

    @NotBlank(message = "Tên sản phẩm không được để trống.")
    @Size(max = 255, message = "Tên sản phẩm không được vượt quá 255 ký tự.")
    private String name;

    @NotNull(message = "Vui lòng chọn danh mục")
    private Long categoryId;

    @NotNull(message = "Vui lòng chọn thương hiệu.")
    private Long brandId;

    private MultipartFile thumbnail;

    private String slug;

    private String thumbnailName;

    private String description;

    private Integer status = 1;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    private List<MultipartFile> images;
    private List<Long> oldFiles;
}
