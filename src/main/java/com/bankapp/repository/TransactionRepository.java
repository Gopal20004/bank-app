package com.bankapp.repository;

import com.bankapp.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Page<Transaction> findByUserIdOrderByTransactionDateDesc(Long userId, Pageable pageable);
    
    List<Transaction> findByUserIdOrderByTransactionDateDesc(Long userId);
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                              @Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.recipientAccountNumber = :accountNumber OR t.senderAccountNumber = :accountNumber ORDER BY t.transactionDate DESC")
    List<Transaction> findByAccountNumber(@Param("accountNumber") String accountNumber);
} 