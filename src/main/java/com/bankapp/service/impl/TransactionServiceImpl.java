package com.bankapp.service.impl;

import com.bankapp.dto.TransferDto;
import com.bankapp.entity.Transaction;
import com.bankapp.entity.User;
import com.bankapp.repository.TransactionRepository;
import com.bankapp.service.TransactionService;
import com.bankapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private UserService userService;
    
    @Override
    public Transaction createDeposit(Long userId, BigDecimal amount, String description) {
        User user = userService.findById(userId);
        
        // Update user balance
        BigDecimal newBalance = user.getBalance().add(amount);
        userService.updateBalance(userId, newBalance);
        
        // Create transaction record
        Transaction transaction = new Transaction(user, Transaction.TransactionType.DEPOSIT, amount, description);
        transaction.setBalanceAfterTransaction(newBalance);
        
        return transactionRepository.save(transaction);
    }
    
    @Override
    public Transaction createWithdrawal(Long userId, BigDecimal amount, String description) {
        User user = userService.findById(userId);
        
        // Check if user has sufficient balance
        if (user.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        
        // Update user balance
        BigDecimal newBalance = user.getBalance().subtract(amount);
        userService.updateBalance(userId, newBalance);
        
        // Create transaction record
        Transaction transaction = new Transaction(user, Transaction.TransactionType.WITHDRAWAL, amount, description);
        transaction.setBalanceAfterTransaction(newBalance);
        
        return transactionRepository.save(transaction);
    }
    
    @Override
    public Transaction transferFunds(Long senderId, TransferDto transferDto) {
        User sender = userService.findById(senderId);
        User recipient = userService.findByAccountNumber(transferDto.getRecipientAccountNumber());
        
        // Check if sender has sufficient balance
        if (sender.getBalance().compareTo(transferDto.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        
        // Check if sender is not transferring to themselves
        if (sender.getAccountNumber().equals(transferDto.getRecipientAccountNumber())) {
            throw new RuntimeException("Cannot transfer to your own account");
        }
        
        // Update sender balance
        BigDecimal senderNewBalance = sender.getBalance().subtract(transferDto.getAmount());
        userService.updateBalance(senderId, senderNewBalance);
        
        // Update recipient balance
        BigDecimal recipientNewBalance = recipient.getBalance().add(transferDto.getAmount());
        userService.updateBalance(recipient.getId(), recipientNewBalance);
        
        // Create transaction record for sender
        Transaction senderTransaction = new Transaction(sender, Transaction.TransactionType.TRANSFER_SENT, 
                                                      transferDto.getAmount(), transferDto.getDescription());
        senderTransaction.setBalanceAfterTransaction(senderNewBalance);
        senderTransaction.setRecipientAccountNumber(transferDto.getRecipientAccountNumber());
        senderTransaction.setSenderAccountNumber(sender.getAccountNumber());
        
        // Create transaction record for recipient
        Transaction recipientTransaction = new Transaction(recipient, Transaction.TransactionType.TRANSFER_RECEIVED, 
                                                          transferDto.getAmount(), transferDto.getDescription());
        recipientTransaction.setBalanceAfterTransaction(recipientNewBalance);
        recipientTransaction.setRecipientAccountNumber(transferDto.getRecipientAccountNumber());
        recipientTransaction.setSenderAccountNumber(sender.getAccountNumber());
        
        // Save both transactions
        transactionRepository.save(recipientTransaction);
        return transactionRepository.save(senderTransaction);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionHistory(Long userId) {
        return transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getTransactionHistoryPaginated(Long userId, Pageable pageable) {
        return transactionRepository.findByUserIdOrderByTransactionDateDesc(userId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionHistoryByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Transaction findById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findByAccountNumber(String accountNumber) {
        return transactionRepository.findByAccountNumber(accountNumber);
    }
} 