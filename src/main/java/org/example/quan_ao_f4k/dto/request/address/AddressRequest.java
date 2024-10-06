package org.example.quan_ao_f4k.dto.request.address;

import lombok.Data;

@Data
public class AddressRequest {
	private String line;
	private String provinceId;
	private String districtId;
	private String wardId;
}
