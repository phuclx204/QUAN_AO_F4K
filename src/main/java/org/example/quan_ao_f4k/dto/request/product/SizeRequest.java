package org.example.quan_ao_f4k.dto.request.product;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SizeRequest {
    @NotBlank(message = "Vui lòng nhập kích cỡ")
    @Size(max = 255, message = "Tên kích cỡ không vượt quá 255 kí tự")
    private String name;

    @NotNull(message = "Vui lòng chọn trạng thái !")
    private Integer status = 1;
}
