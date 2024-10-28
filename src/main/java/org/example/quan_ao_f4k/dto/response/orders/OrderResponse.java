package org.example.quan_ao_f4k.dto.response.orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.quan_ao_f4k.dto.response.authentication.UserResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
	private Long id;
	private UserResponse user;
	private String toName;
	private String toAddress;
	private String toPhone;
	private BigDecimal totalPay;
	private PaymentMethodResponse paymentMethod;
	private Integer paymentStatus;
	private String note;
	private BigDecimal tax;
	private String code;
	private Integer status;
	private String orderType;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
