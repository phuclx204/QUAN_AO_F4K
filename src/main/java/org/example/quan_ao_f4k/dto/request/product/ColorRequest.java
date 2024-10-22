package org.example.quan_ao_f4k.dto.request.product;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class ColorRequest {
    @NotBlank(message = "Tên màu sắc không được để trống.")
    @Size(max = 255, message = "Tên màu sắc không được vượt quá 255 ký tự.")
    private String name;

    @NotBlank(message = "Vui lòng chọn mã màu")
    @Size(min = 7, max = 7, message = "Mã hex phải có đúng 7 ký tự, bao gồm cả dấu #.")
    private String hex;

    @NotNull(message = "Vui lòng chọn trạng thái !")
    private Integer status = 1;
}
