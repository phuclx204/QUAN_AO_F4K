package org.example.quan_ao_f4k.dto.request.address;

import lombok.Data;

@Data
public class DistrictRequest {
	private String name;
	private int code;
	private Long provinceId;
}
