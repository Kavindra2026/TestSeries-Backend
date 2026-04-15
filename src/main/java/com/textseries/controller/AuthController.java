package com.textseries.controller;

import com.textseries.dto.AuthResponseDTO;
import com.textseries.dto.LoginRequestDTO;
import com.textseries.dto.RegisterRequest;
import com.textseries.model.User;
import com.textseries.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String email) {
        return service.sendOtp(email);
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest req) {
        return service.register(req.getUser(), req.getOtp());
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody LoginRequestDTO request) {

        User user = service.getUserByEmail(request.getEmail());  
        String token = service.login(request);

        return new AuthResponseDTO(
                token,
                user.getStudentName(),
                user.getEmail()
        );
    }
}