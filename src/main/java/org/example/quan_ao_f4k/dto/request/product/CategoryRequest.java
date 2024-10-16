package org.example.quan_ao_f4k.dto.request.product;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "Tên danh mục không được để trống.")
    @Size(max = 255, message = "Tên danh mục không được vượt quá 255 ký tự.")
    private String name;

    @NotBlank(message = "Vui lòng nhập mô tả")
    private String description;

    @NotNull(message = "Vui lòng chọn trạng thái !")
    private Integer status = 1;
}
