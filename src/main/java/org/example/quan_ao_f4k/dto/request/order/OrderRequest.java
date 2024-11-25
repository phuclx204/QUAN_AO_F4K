package org.example.quan_ao_f4k.dto.request.order;

import lombok.*;

import javax.annotation.Nullable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @Nullable
    private Long userId;
    private String toName;
    private String toAddress;
    private String toPhone;
    private BigDecimal totalPay;
    @Nullable
    private Long paymentMethodType;
    private Integer paymentStatus;
    private String note;
    private BigDecimal tax;
    @Nullable
    private String code;
    private Integer status=1;
    private String order_type;
}
