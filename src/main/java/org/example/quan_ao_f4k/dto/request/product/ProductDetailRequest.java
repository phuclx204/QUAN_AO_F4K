package org.example.quan_ao_f4k.dto.request.product;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDetailRequest {

    private Long productId;

    @NotNull(message = "Vui lòng chọn kích cỡ")
    private Long sizeId;

    @NotNull(message = "Vui lòng chọn màu sắc")
    private Long colorId;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0.")
    private BigDecimal price;

    @NotNull(message = "Số lượng không được để trống.")
    @Min(value = 0, message = "Số lượng không được âm.")
    private Integer quantity;

    private Integer status = 1;

    private LocalDateTime createdAt = LocalDateTime.now();
}
