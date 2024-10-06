package org.example.quan_ao_f4k.dto.request.address;

import lombok.Data;

@Data
public class WardRequest {
	private String name;
	private int code;
	private Long districtId;
}
