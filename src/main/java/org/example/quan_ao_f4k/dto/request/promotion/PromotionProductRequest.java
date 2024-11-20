package org.example.quan_ao_f4k.dto.request.promotion;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PromotionProductRequest {
    private Long promotionId;
    @NotNull(message = "Sản phẩm không được để trống")
    private Long productId;
    private Integer quantity;
    @NotNull(message = "Vui lòng nhập giá trị giảm")
    private BigDecimal discountValue;
    @NotNull(message = "Loại giảm bắt buộc")
    private Integer type;
    private Integer status;
}
