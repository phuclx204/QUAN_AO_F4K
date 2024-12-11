package org.example.quan_ao_f4k.dto.response.orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdfShopOfflineDTO {
    private String productName;
    private int quantity;
    private BigDecimal price;
    private BigDecimal total;
    private String priceFormatted;
    private String totalFormatted;
}
