package org.example.quan_ao_f4k.dto.request.users;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmployeeRequest {
	private Long userId;

	private BigDecimal salary;

	private Integer employmentType;

	private Integer status;

	private String note;

}
