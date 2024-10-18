package org.example.quan_ao_f4k.dto.request.product;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BrandRequest {

    @NotBlank(message = "Tên thương hiệu không được để trống.")
    @Size(max = 255, message = "Tên thương hiệu không được vượt quá 255 ký tự.")
    private String name;

    @NotNull(message = "Vui lòng chọn trạng thái !")
    private Integer status = 1;
}
