package com.bankapp.service.impl;

import com.bankapp.dto.UserRegistrationDto;
import com.bankapp.entity.User;
import com.bankapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Implementation Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;
    private UserRegistrationDto registrationDto;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setFullName("Test User");
        mockUser.setAccountNumber("ACC001");
        mockUser.setBalance(new BigDecimal("1000.00"));

        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("newuser");
        registrationDto.setEmail("new@example.com");
        registrationDto.setPassword("password123");
        registrationDto.setFullName("New User");
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void registerUser_Success() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        User result = userService.registerUser(registrationDto);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("new@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void registerUser_UsernameExists() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(registrationDto);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void registerUser_EmailExists() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(registrationDto);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("new@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should find user by username successfully")
    void findByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // Act
        User result = userService.findByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw exception when user not found by username")
    void findByUsername_NotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findByUsername("nonexistent");
        });

        assertEquals("User not found with username: nonexistent", exception.getMessage());
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should find user by ID successfully")
    void findById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act
        User result = userService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void findById_NotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(999L);
        });

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("Should find user by account number successfully")
    void findByAccountNumber_Success() {
        // Arrange
        when(userRepository.findByAccountNumber("ACC001")).thenReturn(Optional.of(mockUser));

        // Act
        User result = userService.findByAccountNumber("ACC001");

        // Assert
        assertNotNull(result);
        assertEquals("ACC001", result.getAccountNumber());
        verify(userRepository).findByAccountNumber("ACC001");
    }

    @Test
    @DisplayName("Should throw exception when user not found by account number")
    void findByAccountNumber_NotFound() {
        // Arrange
        when(userRepository.findByAccountNumber("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findByAccountNumber("INVALID");
        });

        assertEquals("User not found with account number: INVALID", exception.getMessage());
        verify(userRepository).findByAccountNumber("INVALID");
    }

    @Test
    @DisplayName("Should get user balance successfully")
    void getBalance_Success() {
        // Arrange
        BigDecimal expectedBalance = new BigDecimal("1000.00");
        when(userRepository.findBalanceByUserId(1L)).thenReturn(Optional.of(expectedBalance));

        // Act
        BigDecimal result = userService.getBalance(1L);

        // Assert
        assertNotNull(result);
        assertEquals(expectedBalance, result);
        verify(userRepository).findBalanceByUserId(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found for balance")
    void getBalance_UserNotFound() {
        // Arrange
        when(userRepository.findBalanceByUserId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getBalance(999L);
        });

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository).findBalanceByUserId(999L);
    }

    @Test
    @DisplayName("Should update user balance successfully")
    void updateBalance_Success() {
        // Arrange
        BigDecimal newBalance = new BigDecimal("1500.00");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        userService.updateBalance(1L, newBalance);

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should get all users successfully")
    void getAllUsers_Success() {
        // Arrange
        List<User> expectedUsers = Arrays.asList(mockUser);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockUser, result.get(0));
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should check if username exists")
    void existsByUsername() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        // Act & Assert
        assertTrue(userService.existsByUsername("testuser"));
        assertFalse(userService.existsByUsername("nonexistent"));

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should check if email exists")
    void existsByEmail() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // Act & Assert
        assertTrue(userService.existsByEmail("test@example.com"));
        assertFalse(userService.existsByEmail("nonexistent@example.com"));

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).existsByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Should check if account number exists")
    void existsByAccountNumber() {
        // Arrange
        when(userRepository.existsByAccountNumber("ACC001")).thenReturn(true);
        when(userRepository.existsByAccountNumber("INVALID")).thenReturn(false);

        // Act & Assert
        assertTrue(userService.existsByAccountNumber("ACC001"));
        assertFalse(userService.existsByAccountNumber("INVALID"));

        verify(userRepository).existsByAccountNumber("ACC001");
        verify(userRepository).existsByAccountNumber("INVALID");
    }

    @Test
    @DisplayName("Should handle registration with null values")
    void registerUser_WithNullValues() {
        // Arrange
        registrationDto.setUsername(null);
        registrationDto.setEmail(null);
        registrationDto.setPassword(null);
        registrationDto.setFullName(null);

        when(userRepository.existsByUsername(null)).thenReturn(false);
        when(userRepository.existsByEmail(null)).thenReturn(false);
        when(passwordEncoder.encode(null)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        User result = userService.registerUser(registrationDto);

        // Assert
        assertNotNull(result);
        verify(userRepository).existsByUsername(null);
        verify(userRepository).existsByEmail(null);
        verify(passwordEncoder).encode(null);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle balance update with negative balance")
    void updateBalance_NegativeBalance() {
        // Arrange
        BigDecimal negativeBalance = new BigDecimal("-100.00");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        userService.updateBalance(1L, negativeBalance);

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle balance update with zero balance")
    void updateBalance_ZeroBalance() {
        // Arrange
        BigDecimal zeroBalance = BigDecimal.ZERO;
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        userService.updateBalance(1L, zeroBalance);

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }
} 