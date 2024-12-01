package org.example.quan_ao_f4k.dto.response.orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatisticsResponse {
	private Long numberOfOrders;
	private BigDecimal totalRevenue;
	private LocalDate orderDate;
}
