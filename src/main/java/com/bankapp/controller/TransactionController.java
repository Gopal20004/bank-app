package com.bankapp.controller;

import com.bankapp.dto.ApiResponse;
import com.bankapp.dto.TransferDto;
import com.bankapp.entity.Transaction;
import com.bankapp.entity.User;
import com.bankapp.service.TransactionService;
import com.bankapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<Transaction>> transfer(@Valid @RequestBody TransferDto transferDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        Transaction transaction = transactionService.transferFunds(user.getId(), transferDto);
        return ResponseEntity.ok(ApiResponse.success("Transfer completed successfully", transaction));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Transaction>>> getTransactionHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionService.getTransactionHistoryPaginated(user.getId(), pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Transaction history retrieved successfully", transactions.getContent()));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Transaction>>> getAllTransactions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        List<Transaction> transactions = transactionService.getTransactionHistory(user.getId());
        return ResponseEntity.ok(ApiResponse.success("All transactions retrieved successfully", transactions));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<Transaction>> getTransactionById(@PathVariable Long transactionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        Transaction transaction = transactionService.findById(transactionId);
        
        // Ensure the transaction belongs to the authenticated user
        if (!transaction.getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Access denied to this transaction"));
        }
        
        return ResponseEntity.ok(ApiResponse.success("Transaction retrieved successfully", transaction));
    }
} 