package org.example.quan_ao_f4k.service.authentication;

import org.example.quan_ao_f4k.dto.request.authentication.RegisterRequest;
import org.example.quan_ao_f4k.dto.response.authentication.AuthenticationResponse;
import org.example.quan_ao_f4k.model.authentication.User;
import org.springframework.stereotype.Service;

import java.util.List;

public interface  AuthenticationService {
    User findByLogin(String login);
    User getUserById(Long userId);
    AuthenticationResponse register(RegisterRequest request);
    List<User> getUsers();
}
