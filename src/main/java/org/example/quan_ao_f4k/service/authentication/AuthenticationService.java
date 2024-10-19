package org.example.quan_ao_f4k.service.authentication;

import org.example.quan_ao_f4k.dto.request.authentication.RegisterRequest;
import org.example.quan_ao_f4k.dto.response.authentication.AuthenticationResponse;
import org.example.quan_ao_f4k.model.authentication.User;

public interface  AuthenticationService {
    User findByLogin(String login);
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse login(RegisterRequest request);
}
