package org.example.quan_ao_f4k.service.authentication;

import lombok.RequiredArgsConstructor;
import org.example.quan_ao_f4k.dto.request.authentication.RegisterRequest;
import org.example.quan_ao_f4k.dto.response.authentication.AuthenticationResponse;
import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.mapper.authentication.UserMapper;
import org.example.quan_ao_f4k.model.authentication.Role;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.repository.authentication.RoleRepository;
import org.example.quan_ao_f4k.repository.authentication.UserRepository;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        Role role = roleRepository.findById(request.getRoleId()).orElseThrow(() -> new IllegalArgumentException("Role not found"));

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Tên đăng nhập đã tồn tại");
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .status(F4KConstants.STATUS_ON)
                .build();

        var savedUser = userRepository.save(user);
        return AuthenticationResponse.builder()
                .status("200")
                .message("Success")
                .build();
    }
}
