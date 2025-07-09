package com.bankapp.service.impl;

import com.bankapp.dto.TransferDto;
import com.bankapp.entity.Transaction;
import com.bankapp.entity.User;
import com.bankapp.repository.TransactionRepository;
import com.bankapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Implementation Tests")
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User sender;
    private User recipient;
    private TransferDto transferDto;
    private Transaction mockTransaction;

    @BeforeEach
    void setUp() {
        // Setup test data
        sender = new User();
        sender.setId(1L);
        sender.setUsername("sender");
        sender.setAccountNumber("ACC001");
        sender.setBalance(new BigDecimal("1000.00"));

        recipient = new User();
        recipient.setId(2L);
        recipient.setUsername("recipient");
        recipient.setAccountNumber("ACC002");
        recipient.setBalance(new BigDecimal("500.00"));

        transferDto = new TransferDto();
        transferDto.setRecipientAccountNumber("ACC002");
        transferDto.setAmount(new BigDecimal("100.00"));
        transferDto.setDescription("Test transfer");

        mockTransaction = new Transaction();
        mockTransaction.setId(1L);
        mockTransaction.setUser(sender);
        mockTransaction.setTransactionType(Transaction.TransactionType.TRANSFER_SENT);
        mockTransaction.setAmount(new BigDecimal("100.00"));
        mockTransaction.setDescription("Test transfer");
    }

    @Test
    @DisplayName("Should successfully transfer funds between users")
    void transferFunds_Success() {
        // Arrange
        when(userService.findById(1L)).thenReturn(sender);
        when(userService.findByAccountNumber("ACC002")).thenReturn(recipient);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        // Act
        Transaction result = transactionService.transferFunds(1L, transferDto);

        // Assert
        assertNotNull(result);
        assertEquals(Transaction.TransactionType.TRANSFER_SENT, result.getTransactionType());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals("Test transfer", result.getDescription());

        // Verify user balance updates
        verify(userService).updateBalance(1L, new BigDecimal("900.00")); // sender balance
        verify(userService).updateBalance(2L, new BigDecimal("600.00")); // recipient balance

        // Verify both transactions are saved
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when sender has insufficient balance")
    void transferFunds_InsufficientBalance() {
        // Arrange
        sender.setBalance(new BigDecimal("50.00")); // Less than transfer amount
        when(userService.findById(1L)).thenReturn(sender);
        when(userService.findByAccountNumber("ACC002")).thenReturn(recipient);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.transferFunds(1L, transferDto);
        });

        assertEquals("Insufficient balance", exception.getMessage());

        // Verify no balance updates or transaction saves
        verify(userService, never()).updateBalance(anyLong(), any(BigDecimal.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when sender tries to transfer to themselves")
    void transferFunds_SelfTransfer() {
        // Arrange
        transferDto.setRecipientAccountNumber("ACC001"); // Same as sender
        when(userService.findById(1L)).thenReturn(sender);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.transferFunds(1L, transferDto);
        });

        assertEquals("Cannot transfer to your own account", exception.getMessage());

        // Verify no balance updates or transaction saves
        verify(userService, never()).updateBalance(anyLong(), any(BigDecimal.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when recipient account not found")
    void transferFunds_RecipientNotFound() {
        // Arrange
        when(userService.findById(1L)).thenReturn(sender);
        when(userService.findByAccountNumber("ACC002"))
            .thenThrow(new RuntimeException("User not found with account number: ACC002"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.transferFunds(1L, transferDto);
        });

        assertEquals("User not found with account number: ACC002", exception.getMessage());

        // Verify no balance updates or transaction saves
        verify(userService, never()).updateBalance(anyLong(), any(BigDecimal.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should create deposit transaction successfully")
    void createDeposit_Success() {
        // Arrange
        BigDecimal depositAmount = new BigDecimal("200.00");
        String description = "Salary deposit";
        when(userService.findById(1L)).thenReturn(sender);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        // Act
        Transaction result = transactionService.createDeposit(1L, depositAmount, description);

        // Assert
        assertNotNull(result);
        verify(userService).updateBalance(1L, new BigDecimal("1200.00")); // 1000 + 200
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should create withdrawal transaction successfully")
    void createWithdrawal_Success() {
        // Arrange
        BigDecimal withdrawalAmount = new BigDecimal("200.00");
        String description = "ATM withdrawal";
        when(userService.findById(1L)).thenReturn(sender);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        // Act
        Transaction result = transactionService.createWithdrawal(1L, withdrawalAmount, description);

        // Assert
        assertNotNull(result);
        verify(userService).updateBalance(1L, new BigDecimal("800.00")); // 1000 - 200
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when withdrawal amount exceeds balance")
    void createWithdrawal_InsufficientBalance() {
        // Arrange
        BigDecimal withdrawalAmount = new BigDecimal("1500.00"); // More than balance
        String description = "Large withdrawal";
        when(userService.findById(1L)).thenReturn(sender);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.createWithdrawal(1L, withdrawalAmount, description);
        });

        assertEquals("Insufficient balance", exception.getMessage());

        // Verify no balance updates or transaction saves
        verify(userService, never()).updateBalance(anyLong(), any(BigDecimal.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should get transaction history for user")
    void getTransactionHistory_Success() {
        // Arrange
        List<Transaction> expectedTransactions = Arrays.asList(mockTransaction);
        when(transactionRepository.findByUserIdOrderByTransactionDateDesc(1L))
            .thenReturn(expectedTransactions);

        // Act
        List<Transaction> result = transactionService.getTransactionHistory(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockTransaction, result.get(0));
        verify(transactionRepository).findByUserIdOrderByTransactionDateDesc(1L);
    }

    @Test
    @DisplayName("Should get paginated transaction history")
    void getTransactionHistoryPaginated_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Transaction> transactions = Arrays.asList(mockTransaction);
        Page<Transaction> expectedPage = new PageImpl<>(transactions, pageable, 1);
        when(transactionRepository.findByUserIdOrderByTransactionDateDesc(1L, pageable))
            .thenReturn(expectedPage);

        // Act
        Page<Transaction> result = transactionService.getTransactionHistoryPaginated(1L, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(transactionRepository).findByUserIdOrderByTransactionDateDesc(1L, pageable);
    }

    @Test
    @DisplayName("Should get transaction history by date range")
    void getTransactionHistoryByDateRange_Success() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<Transaction> expectedTransactions = Arrays.asList(mockTransaction);
        when(transactionRepository.findByUserIdAndDateRange(1L, startDate, endDate))
            .thenReturn(expectedTransactions);

        // Act
        List<Transaction> result = transactionService.getTransactionHistoryByDateRange(1L, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockTransaction, result.get(0));
        verify(transactionRepository).findByUserIdAndDateRange(1L, startDate, endDate);
    }

    @Test
    @DisplayName("Should find transaction by ID successfully")
    void findById_Success() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(mockTransaction));

        // Act
        Transaction result = transactionService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(mockTransaction, result);
        verify(transactionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when transaction not found by ID")
    void findById_NotFound() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.findById(1L);
        });

        assertEquals("Transaction not found with id: 1", exception.getMessage());
        verify(transactionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find transactions by account number")
    void findByAccountNumber_Success() {
        // Arrange
        String accountNumber = "ACC001";
        List<Transaction> expectedTransactions = Arrays.asList(mockTransaction);
        when(transactionRepository.findByAccountNumber(accountNumber))
            .thenReturn(expectedTransactions);

        // Act
        List<Transaction> result = transactionService.findByAccountNumber(accountNumber);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockTransaction, result.get(0));
        verify(transactionRepository).findByAccountNumber(accountNumber);
    }

    @Test
    @DisplayName("Should handle zero amount transfer")
    void transferFunds_ZeroAmount() {
        // Arrange
        transferDto.setAmount(BigDecimal.ZERO);
        when(userService.findById(1L)).thenReturn(sender);
        when(userService.findByAccountNumber("ACC002")).thenReturn(recipient);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        // Act
        Transaction result = transactionService.transferFunds(1L, transferDto);

        // Assert
        assertNotNull(result);
        verify(userService).updateBalance(1L, new BigDecimal("1000.00")); // No change
        verify(userService).updateBalance(2L, new BigDecimal("500.00")); // No change
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should handle transfer with null description")
    void transferFunds_NullDescription() {
        // Arrange
        transferDto.setDescription(null);
        when(userService.findById(1L)).thenReturn(sender);
        when(userService.findByAccountNumber("ACC002")).thenReturn(recipient);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        // Act
        Transaction result = transactionService.transferFunds(1L, transferDto);

        // Assert
        assertNotNull(result);
        verify(userService).updateBalance(1L, new BigDecimal("900.00"));
        verify(userService).updateBalance(2L, new BigDecimal("600.00"));
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should handle large transfer amount")
    void transferFunds_LargeAmount() {
        // Arrange
        transferDto.setAmount(new BigDecimal("999.99")); // Almost entire balance
        when(userService.findById(1L)).thenReturn(sender);
        when(userService.findByAccountNumber("ACC002")).thenReturn(recipient);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        // Act
        Transaction result = transactionService.transferFunds(1L, transferDto);

        // Assert
        assertNotNull(result);
        verify(userService).updateBalance(1L, new BigDecimal("0.01")); // Remaining balance
        verify(userService).updateBalance(2L, new BigDecimal("1499.99")); // New balance
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should verify transaction properties are set correctly")
    void transferFunds_TransactionProperties() {
        // Arrange
        when(userService.findById(1L)).thenReturn(sender);
        when(userService.findByAccountNumber("ACC002")).thenReturn(recipient);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction savedTransaction = invocation.getArgument(0);
            savedTransaction.setId(1L);
            return savedTransaction;
        });

        // Act
        Transaction result = transactionService.transferFunds(1L, transferDto);

        // Assert
        assertNotNull(result);
        assertEquals(sender, result.getUser());
        assertEquals(Transaction.TransactionType.TRANSFER_SENT, result.getTransactionType());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals("Test transfer", result.getDescription());
        assertEquals("ACC002", result.getRecipientAccountNumber());
        assertEquals("ACC001", result.getSenderAccountNumber());
        assertEquals(new BigDecimal("900.00"), result.getBalanceAfterTransaction());
        assertNotNull(result.getTransactionDate());
        assertEquals(Transaction.TransactionStatus.COMPLETED, result.getStatus());
    }
} 