package org.example.quan_ao_f4k.controller.authentication;

import org.example.quan_ao_f4k.dto.request.authentication.RegisterRequest;
import org.example.quan_ao_f4k.service.authentication.AuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/auth")
@Controller
public class AuthenticationController {

    @Autowired
    private AuthenticationServiceImpl authenticationService;

    @PostMapping("/register")
    public String register(@ModelAttribute("user") RegisterRequest registerRequest) {
        authenticationService.register(registerRequest);
        return "redirect:/auth/login?registerSuccess";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new RegisterRequest());
        return "/shop/pages/register";
    }

    @GetMapping("/login")
    public String login() {
        return "/shop/pages/login";
    }
}
