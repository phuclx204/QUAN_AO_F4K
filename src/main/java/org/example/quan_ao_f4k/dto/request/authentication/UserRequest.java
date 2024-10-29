package org.example.quan_ao_f4k.dto.request.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

	@NotBlank(message = "Email không được để trống")
	private String email;

	private Long addressId;

	private Long roleId=2L;//Mắc định là user

	@NotBlank(message = "Tên đăng nhập không được để trống")
	@Size(min = 3, max = 20, message = "Tên đăng nhập phải từ 3 đến 20 ký tự")
	private String username;

	@NotBlank(message = "Mật khẩu không được để trống")
	@Size(min = 5, message = "Mật khẩu phải tối thiểu 6  ký tự")
	private String password;

	@NotBlank(message = "Xác nhận mật khẩu không được để trống")
	private String confirmPassword;

	@NotBlank(message = "Số điện thoại không được để trống")
	private String numberPhone;

	private int status = 1;

}
