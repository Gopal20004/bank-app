package com.bankapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class TransferDto {
    
    @NotBlank(message = "Recipient account number is required")
    private String recipientAccountNumber;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    private String description;
    
    // Constructors
    public TransferDto() {}
    
    public TransferDto(String recipientAccountNumber, BigDecimal amount, String description) {
        this.recipientAccountNumber = recipientAccountNumber;
        this.amount = amount;
        this.description = description;
    }
    
    // Getters and Setters
    public String getRecipientAccountNumber() {
        return recipientAccountNumber;
    }
    
    public void setRecipientAccountNumber(String recipientAccountNumber) {
        this.recipientAccountNumber = recipientAccountNumber;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
} 