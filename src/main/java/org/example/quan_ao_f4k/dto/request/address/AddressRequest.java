package org.example.quan_ao_f4k.dto.request.address;

import lombok.Data;

@Data
public class AddressRequest {
	private String line;
	private Long provinceId;
	private Long districtId;
	private Long wardId;
}
