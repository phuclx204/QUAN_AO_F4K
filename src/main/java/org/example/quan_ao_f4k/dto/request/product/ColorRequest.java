package org.example.quan_ao_f4k.dto.request.product;

import lombok.Data;
import jakarta.validation.constraints.*;


@Data
public class ColorRequest {
    @NotBlank(message = "Tên màu sắc không được để trống.")
    @Size(max = 255, message = "Tên màu sắc không được vượt quá 255 ký tự.")
    private String name;

    @NotNull(message = "Vui lòng chọn trạng thái !")
    private Integer status = 1;
}
