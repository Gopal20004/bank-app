package com.bankapp.service.impl;

import com.bankapp.dto.LoginDto;
import com.bankapp.dto.UserRegistrationDto;
import com.bankapp.entity.User;
import com.bankapp.security.JwtUtil;
import com.bankapp.service.AuthService;
import com.bankapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public String login(LoginDto loginDto) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );

        // Get user details
        User user = userService.findByUsername(loginDto.getUsername());

        // Generate JWT token
        return jwtUtil.generateToken(user.getUsername());
    }

    @Override
    public User register(UserRegistrationDto registrationDto) {
        return userService.registerUser(registrationDto);
    }

    @Override
    public String generateToken(User user) {
        return jwtUtil.generateToken(user.getUsername());
    }

    @Override
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }
} 