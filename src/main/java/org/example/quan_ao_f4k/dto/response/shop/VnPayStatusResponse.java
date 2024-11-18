package org.example.quan_ao_f4k.dto.response.shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VnPayStatusResponse {
    private boolean isSuccess;
    private String transactionCode;
    private String message;
    private String maHoaDon;
}
