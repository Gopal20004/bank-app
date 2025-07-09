package com.bankapp.service;

import com.bankapp.dto.TransferDto;
import com.bankapp.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    
    Transaction createDeposit(Long userId, BigDecimal amount, String description);
    
    Transaction createWithdrawal(Long userId, BigDecimal amount, String description);
    
    Transaction transferFunds(Long senderId, TransferDto transferDto);
    
    List<Transaction> getTransactionHistory(Long userId);
    
    Page<Transaction> getTransactionHistoryPaginated(Long userId, Pageable pageable);
    
    List<Transaction> getTransactionHistoryByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    Transaction findById(Long transactionId);
    
    List<Transaction> findByAccountNumber(String accountNumber);
} 