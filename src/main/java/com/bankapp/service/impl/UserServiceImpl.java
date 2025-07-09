package com.bankapp.service.impl;

import com.bankapp.dto.UserRegistrationDto;
import com.bankapp.entity.User;
import com.bankapp.repository.UserRepository;
import com.bankapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public User registerUser(UserRegistrationDto registrationDto) {
        // Check if username already exists
        if (existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email already exists
        if (existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create new user
        User user = new User(
            registrationDto.getUsername(),
            registrationDto.getEmail(),
            passwordEncoder.encode(registrationDto.getPassword()),
            registrationDto.getFullName()
        );
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
    
    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public User findByAccountNumber(String accountNumber) {
        return userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("User not found with account number: " + accountNumber));
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long userId) {
        return userRepository.findBalanceByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalance(String username) {
        User user = findByUsername(username);
        return user.getBalance();
    }
    
    @Override
    public void updateBalance(Long userId, BigDecimal newBalance) {
        User user = findById(userId);
        user.setBalance(newBalance);
        userRepository.save(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByAccountNumber(String accountNumber) {
        return userRepository.existsByAccountNumber(accountNumber);
    }
    
    @Override
    public BigDecimal deposit(String username, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be greater than zero");
        }
        
        User user = findByUsername(username);
        BigDecimal newBalance = user.getBalance().add(amount);
        user.setBalance(newBalance);
        userRepository.save(user);
        
        return newBalance;
    }
} 