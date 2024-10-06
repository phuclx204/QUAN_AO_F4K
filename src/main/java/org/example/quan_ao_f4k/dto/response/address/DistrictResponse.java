package org.example.quan_ao_f4k.dto.response.address;

import lombok.Data;

@Data
public class DistrictResponse {
	private Long id;
	private String name;
	private int code;
	private ProvinceResponse province;
}
