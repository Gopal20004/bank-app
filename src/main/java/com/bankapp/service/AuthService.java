package com.bankapp.service;

import com.bankapp.dto.LoginDto;
import com.bankapp.dto.UserRegistrationDto;
import com.bankapp.entity.User;

public interface AuthService {
    String login(LoginDto loginDto);
    User register(UserRegistrationDto registrationDto);
    String generateToken(User user);
    boolean validateToken(String token);
    String getUsernameFromToken(String token);
} 