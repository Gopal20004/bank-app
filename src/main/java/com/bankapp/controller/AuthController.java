package com.bankapp.controller;

import com.bankapp.dto.ApiResponse;
import com.bankapp.dto.LoginDto;
import com.bankapp.dto.UserRegistrationDto;
import com.bankapp.entity.User;
import com.bankapp.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        User user = authService.register(registrationDto);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);
        return ResponseEntity.ok(ApiResponse.success("Login successful", token));
    }
} 