package com.bankapp.service;

import com.bankapp.dto.UserRegistrationDto;
import com.bankapp.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    
    User registerUser(UserRegistrationDto registrationDto);
    
    User findByUsername(String username);
    
    User findById(Long id);
    
    User findByAccountNumber(String accountNumber);
    
    BigDecimal getBalance(Long userId);
    
    BigDecimal getBalance(String username);
    
    void updateBalance(Long userId, BigDecimal newBalance);
    
    BigDecimal deposit(String username, BigDecimal amount);
    
    List<User> getAllUsers();
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByAccountNumber(String accountNumber);
} 