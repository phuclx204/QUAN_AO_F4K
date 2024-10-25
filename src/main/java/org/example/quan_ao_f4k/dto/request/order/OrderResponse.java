package org.example.quan_ao_f4k.dto.request.order;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderResponse {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long addressId;
    private Long userId;
    private String toName;
    private String toAddress;
    private String toPhone;
    private BigDecimal totalPay;
    private Long paymentMethodType;
    private Integer paymentStatus;
    private String note;
    private BigDecimal tax;
    private String code;
    private Integer status;
}
