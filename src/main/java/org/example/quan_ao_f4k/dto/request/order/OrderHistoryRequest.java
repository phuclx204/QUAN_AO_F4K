package org.example.quan_ao_f4k.dto.request.order;

import lombok.Data;

@Data
public class OrderHistoryRequest {
    private Long orderId;
    private Integer status;
    private String note;
}
