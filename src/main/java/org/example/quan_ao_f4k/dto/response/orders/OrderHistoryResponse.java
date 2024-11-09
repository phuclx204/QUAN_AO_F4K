package org.example.quan_ao_f4k.dto.response.orders;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderHistoryResponse {
    private Long id;
    private Long orderId;
    private Integer status;
    private String note;
    private LocalDateTime changeDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
