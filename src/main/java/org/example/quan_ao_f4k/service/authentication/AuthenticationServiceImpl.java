package org.example.quan_ao_f4k.service.authentication;

import lombok.RequiredArgsConstructor;
import org.example.quan_ao_f4k.dto.request.authentication.LoginRequest;
import org.example.quan_ao_f4k.dto.request.authentication.RegisterRequest;
import org.example.quan_ao_f4k.dto.response.authentication.AuthenticationResponse;
import org.example.quan_ao_f4k.model.authentication.Role;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.repository.authentication.RoleRepository;
import org.example.quan_ao_f4k.repository.authentication.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private RefreshTokenService refreshTokenService;
	@Autowired
	private AuthenticationManager authenticationManagerBean;
	@Autowired
	private RoleRepository roleRepository;


	@Override
	public AuthenticationResponse register(RegisterRequest registerRequest) {
		Role role = roleRepository.findById(registerRequest.getRoleId())
				.orElseThrow(() -> new IllegalArgumentException("Role not found"));
		Optional<User> userOp = userRepository.findByUsername(registerRequest.getUsername());
		if (userOp.isPresent()) {
			throw new IllegalArgumentException("Username already exists");
		}

		var user = User.builder()
				.username(registerRequest.getUsername())
				.email(registerRequest.getEmail())
				.password(passwordEncoder.encode(registerRequest.getPassword()))
				.role(role)
				.status(1)
				.build();
		User user1 = userRepository.save(user);

		var accessToken = jwtService.generateToken(user1);
		var refreshToken = refreshTokenService.createRefreshToken(user1.getUsername());
		return AuthenticationResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken.getToken())
				.build();
	}

	@Override
	public AuthenticationResponse login(LoginRequest loginRequest) {
		try {
			authenticationManagerBean.authenticate(
					new UsernamePasswordAuthenticationToken(
							loginRequest.getUsername(),
							loginRequest.getPassword()
					));
		} catch (BadCredentialsException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid username or password");
		}

		var user = userRepository.findByUsername(loginRequest.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("Username not found"));

		var accessToken = jwtService.generateToken(user);
		var refreshToken = refreshTokenService.createRefreshToken(loginRequest.getUsername());
		return AuthenticationResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken.getToken())
				.build();
	}
}
