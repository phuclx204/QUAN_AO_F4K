package org.example.quan_ao_f4k.dto.request.order;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.math.BigDecimal;

@Getter
@Setter
public class OrderRequest {
    @Nullable
    private Long addressId;
    @Nullable
    private Long userId;
    private String toName;
    private String toAddress;
    private String toPhone;
    private BigDecimal totalPay;
    private Long paymentMethodType;
    private String note;
}
