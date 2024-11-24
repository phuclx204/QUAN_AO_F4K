package org.example.quan_ao_f4k.dto.response.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
	private Long id;
	private CustomerResponse user;

	private BigDecimal salary;

	private Integer employmentType;

	private Integer status;

	private String note;

}
