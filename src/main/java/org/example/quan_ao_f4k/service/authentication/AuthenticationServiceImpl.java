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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    private Role getRole() {
        Role role = roleRepository.findByName(F4KConstants.ROLE_USER);
        if (role == null) {
            return roleRepository.save(
                    Role.builder()
                            .name(F4KConstants.ROLE_USER)
                            .status(F4KConstants.STATUS_ON)
                            .build());
        }
        return role;
    }

    @Override
    public User findByLogin(String login) {
        Optional<User> userOptional = userRepository.findByUsername(login);
        return userOptional
                .orElseThrow(() -> new IllegalStateException("User with username: " + login + " does not exist"));
    }

    @Override
    public User getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional
                .orElseThrow(() -> new IllegalStateException("User with id: " + userId + " does not exist"));
    }

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already in use");
        }

        User newUser = User
                .builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(getRole())
                .email(request.getEmail())
                .status(F4KConstants.STATUS_ON)
                .build();

        User createdUser = userRepository.save(newUser);

        return AuthenticationResponse
                .builder()
                .userDto(userMapper.entityToResponse(createdUser))
                .build();
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
