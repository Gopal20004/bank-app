package com.bankapp.controller;

import com.bankapp.dto.ApiResponse;
import com.bankapp.entity.User;
import com.bankapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<BigDecimal>> getBalance() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        BigDecimal balance = userService.getBalance(username);
        return ResponseEntity.ok(ApiResponse.success("Balance retrieved successfully", balance));
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<BigDecimal>> deposit(@RequestBody DepositRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        BigDecimal newBalance = userService.deposit(username, request.getAmount());
        return ResponseEntity.ok(ApiResponse.success("Deposit successful", newBalance));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", user));
    }

    public static class DepositRequest {
        private BigDecimal amount;

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
} 