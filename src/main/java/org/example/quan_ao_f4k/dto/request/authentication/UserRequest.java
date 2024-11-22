package org.example.quan_ao_f4k.dto.request.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

	private String email;

	private Long addressId;

	private Long roleId=2L;//Mắc định là user

	private String username;

	private String password;
}
