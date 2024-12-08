package org.example.quan_ao_f4k.dto.response.orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.quan_ao_f4k.dto.response.authentication.UserResponse;
import org.example.quan_ao_f4k.model.order.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
	private Long id;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Long userId;
	private String toName;
	private String toAddress;
	private String toPhone;
	private Long paymentMethodType;
	private Integer paymentStatus;
	private String note;
	private BigDecimal tax;
	private String code;
	private Integer status;
	private String statusText;
	private String order_type;

	private PaymentMethod paymentMethod;
	private BigDecimal totalPay;
	private BigDecimal shippingPay;
	private BigDecimal totalCart;
}
