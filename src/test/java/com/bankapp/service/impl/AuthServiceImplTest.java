package com.bankapp.service.impl;

import com.bankapp.dto.LoginDto;
import com.bankapp.dto.UserRegistrationDto;
import com.bankapp.entity.User;
import com.bankapp.service.UserService;
import com.bankapp.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Implementation Tests")
class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private User mockUser;
    private LoginDto loginDto;
    private UserRegistrationDto registrationDto;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setFullName("Test User");
        mockUser.setAccountNumber("ACC001");

        loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password123");

        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("newuser");
        registrationDto.setEmail("new@example.com");
        registrationDto.setPassword("password123");
        registrationDto.setFullName("New User");
    }

    @Test
    @DisplayName("Should successfully login user")
    void login_Success() {
        // Arrange
        String expectedToken = "jwt.token.here";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userService.findByUsername("testuser")).thenReturn(mockUser);
        when(jwtUtil.generateToken("testuser")).thenReturn(expectedToken);

        // Act
        String result = authService.login(loginDto);

        // Assert
        assertNotNull(result);
        assertEquals(expectedToken, result);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService).findByUsername("testuser");
        verify(jwtUtil).generateToken("testuser");
    }

    @Test
    @DisplayName("Should successfully register new user")
    void register_Success() {
        // Arrange
        when(userService.registerUser(registrationDto)).thenReturn(mockUser);

        // Act
        User result = authService.register(registrationDto);

        // Assert
        assertNotNull(result);
        assertEquals(mockUser, result);
        verify(userService).registerUser(registrationDto);
    }

    @Test
    @DisplayName("Should generate token for user")
    void generateToken_Success() {
        // Arrange
        String expectedToken = "generated.jwt.token";
        when(jwtUtil.generateToken("testuser")).thenReturn(expectedToken);

        // Act
        String result = authService.generateToken(mockUser);

        // Assert
        assertNotNull(result);
        assertEquals(expectedToken, result);
        verify(jwtUtil).generateToken("testuser");
    }

    @Test
    @DisplayName("Should validate token successfully")
    void validateToken_Success() {
        // Arrange
        String token = "valid.jwt.token";
        when(jwtUtil.validateToken(token)).thenReturn(true);

        // Act
        boolean result = authService.validateToken(token);

        // Assert
        assertTrue(result);
        verify(jwtUtil).validateToken(token);
    }

    @Test
    @DisplayName("Should return false for invalid token")
    void validateToken_Invalid() {
        // Arrange
        String token = "invalid.jwt.token";
        when(jwtUtil.validateToken(token)).thenReturn(false);

        // Act
        boolean result = authService.validateToken(token);

        // Assert
        assertFalse(result);
        verify(jwtUtil).validateToken(token);
    }

    @Test
    @DisplayName("Should get username from token")
    void getUsernameFromToken_Success() {
        // Arrange
        String token = "jwt.token.here";
        String expectedUsername = "testuser";
        when(jwtUtil.getUsernameFromToken(token)).thenReturn(expectedUsername);

        // Act
        String result = authService.getUsernameFromToken(token);

        // Assert
        assertNotNull(result);
        assertEquals(expectedUsername, result);
        verify(jwtUtil).getUsernameFromToken(token);
    }

    @Test
    @DisplayName("Should handle login with null credentials")
    void login_WithNullCredentials() {
        // Arrange
        loginDto.setUsername(null);
        loginDto.setPassword(null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userService.findByUsername(null)).thenReturn(mockUser);
        when(jwtUtil.generateToken("testuser")).thenReturn("token");

        // Act
        String result = authService.login(loginDto);

        // Assert
        assertNotNull(result);
        assertEquals("token", result);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService).findByUsername(null);
        verify(jwtUtil).generateToken("testuser");
    }

    @Test
    @DisplayName("Should handle registration with null values")
    void register_WithNullValues() {
        // Arrange
        registrationDto.setUsername(null);
        registrationDto.setEmail(null);
        registrationDto.setPassword(null);
        registrationDto.setFullName(null);

        when(userService.registerUser(registrationDto)).thenReturn(mockUser);

        // Act
        User result = authService.register(registrationDto);

        // Assert
        assertNotNull(result);
        verify(userService).registerUser(registrationDto);
    }

    @Test
    @DisplayName("Should handle empty token validation")
    void validateToken_EmptyToken() {
        // Arrange
        String emptyToken = "";
        when(jwtUtil.validateToken(emptyToken)).thenReturn(false);

        // Act
        boolean result = authService.validateToken(emptyToken);

        // Assert
        assertFalse(result);
        verify(jwtUtil).validateToken(emptyToken);
    }

    @Test
    @DisplayName("Should handle null token validation")
    void validateToken_NullToken() {
        // Arrange
        when(jwtUtil.validateToken(null)).thenReturn(false);

        // Act
        boolean result = authService.validateToken(null);

        // Assert
        assertFalse(result);
        verify(jwtUtil).validateToken(null);
    }

    @Test
    @DisplayName("Should handle empty username from token")
    void getUsernameFromToken_EmptyUsername() {
        // Arrange
        String token = "jwt.token.here";
        when(jwtUtil.getUsernameFromToken(token)).thenReturn("");

        // Act
        String result = authService.getUsernameFromToken(token);

        // Assert
        assertNotNull(result);
        assertEquals("", result);
        verify(jwtUtil).getUsernameFromToken(token);
    }

    @Test
    @DisplayName("Should handle null username from token")
    void getUsernameFromToken_NullUsername() {
        // Arrange
        String token = "jwt.token.here";
        when(jwtUtil.getUsernameFromToken(token)).thenReturn(null);

        // Act
        String result = authService.getUsernameFromToken(token);

        // Assert
        assertNull(result);
        verify(jwtUtil).getUsernameFromToken(token);
    }

    @Test
    @DisplayName("Should verify authentication token creation")
    void login_VerifyAuthenticationToken() {
        // Arrange
        String expectedToken = "jwt.token.here";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userService.findByUsername("testuser")).thenReturn(mockUser);
        when(jwtUtil.generateToken("testuser")).thenReturn(expectedToken);

        // Act
        authService.login(loginDto);

        // Assert
        verify(authenticationManager).authenticate(argThat(token -> 
            token instanceof UsernamePasswordAuthenticationToken &&
            "testuser".equals(token.getPrincipal()) &&
            "password123".equals(token.getCredentials())
        ));
    }

    @Test
    @DisplayName("Should handle authentication failure")
    void login_AuthenticationFailure() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new RuntimeException("Authentication failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginDto);
        });

        assertEquals("Authentication failed", exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, never()).findByUsername(anyString());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Should handle user not found during login")
    void login_UserNotFound() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userService.findByUsername("testuser"))
            .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginDto);
        });

        assertEquals("User not found", exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService).findByUsername("testuser");
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Should handle JWT generation failure")
    void login_JwtGenerationFailure() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userService.findByUsername("testuser")).thenReturn(mockUser);
        when(jwtUtil.generateToken("testuser"))
            .thenThrow(new RuntimeException("JWT generation failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginDto);
        });

        assertEquals("JWT generation failed", exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService).findByUsername("testuser");
        verify(jwtUtil).generateToken("testuser");
    }
} 