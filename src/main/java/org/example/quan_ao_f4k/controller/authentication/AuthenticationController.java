package org.example.quan_ao_f4k.controller.authentication;

import jakarta.validation.Valid;
import org.example.quan_ao_f4k.dto.request.authentication.LoginRequest;
import org.example.quan_ao_f4k.dto.request.authentication.RefreshTokenRequest;
import org.example.quan_ao_f4k.dto.request.authentication.RegisterRequest;
import org.example.quan_ao_f4k.dto.response.authentication.AuthenticationResponse;
import org.example.quan_ao_f4k.model.authentication.RefreshToken;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.repository.authentication.RefreshTokenRepository;
import org.example.quan_ao_f4k.service.authentication.AuthenticationService;
import org.example.quan_ao_f4k.service.authentication.AuthenticationServiceImpl;
import org.example.quan_ao_f4k.service.authentication.JwtService;
import org.example.quan_ao_f4k.service.authentication.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/auth")
@Controller
public class AuthenticationController {
	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	private RefreshTokenService refreshTokenService;
	@Autowired
	private JwtService jwtService;


	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("user", new RegisterRequest());
		return "/shop/pages/register";
	}

	@GetMapping("/login")
	public String login() {
		return "/shop/pages/login";
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest, BindingResult result) {
		if (result.hasErrors()) {
			List<String> errors = result.getAllErrors().stream()
					.map(error -> error.getDefaultMessage())
					.collect(Collectors.toList());
			return ResponseEntity.badRequest().body(errors);
		}

		AuthenticationResponse response = authenticationService.register(registerRequest);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/authen")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {
		if (result.hasErrors()) {
			List<String> errors = result.getAllErrors().stream()
					.map(error -> error.getDefaultMessage())
					.collect(Collectors.toList());
			return ResponseEntity.badRequest().body(errors);
		}
		AuthenticationResponse response = authenticationService.login(loginRequest);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/refreshToken")
	public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
		RefreshToken refreshToken = refreshTokenService.verifiRefreshToken(refreshTokenRequest.getToken());
		User user = refreshToken.getUser();

		String accessToken = jwtService.generateToken(user);
		return ResponseEntity.ok(AuthenticationResponse.builder()
						.accessToken(accessToken)
						.refreshToken(refreshToken.getToken())
				.build());
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestParam String refreshToken) {
		refreshTokenService.deleteRefreshToken(refreshToken);
		return ResponseEntity.ok("Logged out successfully.");
	}

}
