package org.example.quan_ao_f4k.dto.request.order;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderDetailResponse {
    private Long orderId;
    private Long productDetailId;
    private Integer quantity;
    private BigDecimal price;
}
