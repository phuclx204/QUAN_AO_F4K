package org.example.quan_ao_f4k.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryDto {
    private Long id; //
    private Long orderId;
    private Integer status;
    private String note; // Ghi chú

}
