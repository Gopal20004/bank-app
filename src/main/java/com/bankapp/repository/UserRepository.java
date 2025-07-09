package com.bankapp.repository;

import com.bankapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByAccountNumber(String accountNumber);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByAccountNumber(String accountNumber);
    
    @Query("SELECT u.balance FROM User u WHERE u.id = :userId")
    Optional<java.math.BigDecimal> findBalanceByUserId(@Param("userId") Long userId);
} 