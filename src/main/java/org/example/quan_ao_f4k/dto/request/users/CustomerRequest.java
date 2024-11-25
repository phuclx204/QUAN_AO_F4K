package org.example.quan_ao_f4k.dto.request.users;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequest {
	private Long id;
	private String email;

	@Nullable
	private Long addressId=15L;

	private String username;

	private String password;

	@NotBlank(message = "Vui lòng nhập số điện thoại")
	private String numberPhone;

	@NotBlank(message = "Vui lòng nhập tên")
	private String fullName;

	private Integer gender;

	private LocalDate birthDate;

	@Nullable
	private String avatarUrl;

	private Integer status=1;

	private Long roleId=2L;
}
