package org.example.quan_ao_f4k.dto.response.authentication;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.quan_ao_f4k.dto.response.address.AddressResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
	private Long id;
	private String email;
	private String username;
	private String numberPhone;
	private AddressResponse address;
	private RoleResponse role;
	private Integer status;
}