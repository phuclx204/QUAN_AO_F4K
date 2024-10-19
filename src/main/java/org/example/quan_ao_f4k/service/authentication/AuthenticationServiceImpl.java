package org.example.quan_ao_f4k.service.authentication;

import lombok.SneakyThrows;
import org.example.quan_ao_f4k.dto.request.authentication.RegisterRequest;
import org.example.quan_ao_f4k.dto.response.authentication.AuthenticationResponse;
import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.mapper.authentication.UserMapper;
import org.example.quan_ao_f4k.model.authentication.Role;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.repository.authentication.RoleRepository;
import org.example.quan_ao_f4k.repository.authentication.UserRepository;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


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
        return userRepository.findByUsername(login)
                .orElse(null);
    }

    @Override
    public AuthenticationResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already in use");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEmail(request.getEmail());
        newUser.setRole(getRole());

        User createdUser = userRepository.save(newUser);

        return AuthenticationResponse
                .builder()
                .userDto(userMapper.entityToResponse(createdUser))
                .build();
    }

    @SneakyThrows
    @Override
    public AuthenticationResponse login(RegisterRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(
                        () -> new Exception("Username or password is incorrect")
                );

        return AuthenticationResponse.builder()
                .userDto(userMapper.entityToResponse(user))
                .build();
    }
}
