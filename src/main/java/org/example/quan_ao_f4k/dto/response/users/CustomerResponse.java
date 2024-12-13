package org.example.quan_ao_f4k.dto.response.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.quan_ao_f4k.dto.response.address.AddressResponse;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
	private Long id;

	private String email;

	private AddressResponse address;

	private String username;

	private String password;

	private String numberPhone;

	private String fullName;

	private Integer gender;

	private LocalDate birthDate;

	private String avatarUrl;

	private Integer status=1;

	private String addressDetail;

}
