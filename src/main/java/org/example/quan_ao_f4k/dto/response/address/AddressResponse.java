package org.example.quan_ao_f4k.dto.response.address;

import lombok.Data;

@Data
public class AddressResponse {
	private Long id;
	private String line;
	private ProvinceResponse province;
	private DistrictResponse district;
	private WardResponse ward;
}


