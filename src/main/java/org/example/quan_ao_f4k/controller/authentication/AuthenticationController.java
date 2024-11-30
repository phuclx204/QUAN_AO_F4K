package org.example.quan_ao_f4k.controller.authentication;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.quan_ao_f4k.dto.request.authentication.RegisterRequest;
import org.example.quan_ao_f4k.service.authentication.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new RegisterRequest());
        return "/verify_account/register";
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerApi(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    @GetMapping("/login")
    public String login() {
        return "/verify_account/login";
    }

//
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(@RequestParam String refreshToken) {
//        refreshTokenService.deleteRefreshToken(refreshToken);
//        return ResponseEntity.ok("Logged out successfully.");
//    }
}
