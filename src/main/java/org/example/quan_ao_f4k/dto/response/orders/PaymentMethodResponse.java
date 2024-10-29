package org.example.quan_ao_f4k.dto.response.orders;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodResponse {
	private Long id;
	private String name;
	private Integer status;
}
