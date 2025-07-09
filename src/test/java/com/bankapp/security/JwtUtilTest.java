package com.bankapp.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JWT Utility Tests")
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private static final String TEST_SECRET = "testSecretKeyForJwtTokenGenerationAndValidation123456789";
    private static final long TEST_EXPIRATION = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", TEST_EXPIRATION);
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void generateToken_Success() {
        // Arrange
        String username = "testuser";

        // Act
        String token = jwtUtil.generateToken(username);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    @DisplayName("Should generate different tokens for different usernames")
    void generateToken_DifferentUsernames() {
        // Arrange
        String username1 = "user1";
        String username2 = "user2";

        // Act
        String token1 = jwtUtil.generateToken(username1);
        String token2 = jwtUtil.generateToken(username2);

        // Assert
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Should extract username from valid token")
    void getUsernameFromToken_Success() {
        // Arrange
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // Act
        String extractedUsername = jwtUtil.getUsernameFromToken(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("Should validate valid token")
    void validateToken_ValidToken() {
        // Arrange
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // Act
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should reject invalid token")
    void validateToken_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should reject null token")
    void validateToken_NullToken() {
        // Act
        boolean isValid = jwtUtil.validateToken(null);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should reject empty token")
    void validateToken_EmptyToken() {
        // Act
        boolean isValid = jwtUtil.validateToken("");

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should reject malformed token")
    void validateToken_MalformedToken() {
        // Arrange
        String malformedToken = "not.a.valid.jwt.token";

        // Act
        boolean isValid = jwtUtil.validateToken(malformedToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle token with wrong signature")
    void validateToken_WrongSignature() {
        // Arrange
        String tokenWithWrongSignature = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYxNjE2MjQwMCwiZXhwIjoxNjE2MTY2MDAwfQ.wrongsignature";

        // Act
        boolean isValid = jwtUtil.validateToken(tokenWithWrongSignature);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle expired token")
    void validateToken_ExpiredToken() {
        // Arrange - Create a token with very short expiration
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 1L); // 1ms expiration
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle token with null username")
    void generateToken_NullUsername() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.generateToken(null);
        });
    }

    @Test
    @DisplayName("Should handle token with empty username")
    void generateToken_EmptyUsername() {
        // Act
        String token = jwtUtil.generateToken("");

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Should extract username from token with special characters")
    void getUsernameFromToken_SpecialCharacters() {
        // Arrange
        String username = "user@domain.com";
        String token = jwtUtil.generateToken(username);

        // Act
        String extractedUsername = jwtUtil.getUsernameFromToken(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("Should handle token with very long username")
    void generateToken_LongUsername() {
        // Arrange
        String longUsername = "a".repeat(1000);

        // Act
        String token = jwtUtil.generateToken(longUsername);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(longUsername, jwtUtil.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("Should generate tokens with consistent format")
    void generateToken_ConsistentFormat() {
        // Arrange
        String username1 = "testuser1";
        String username2 = "testuser2";

        // Act
        String token1 = jwtUtil.generateToken(username1);
        String token2 = jwtUtil.generateToken(username2);

        // Assert
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2); // Different usernames should produce different tokens
        assertTrue(token1.split("\\.").length == 3);
        assertTrue(token2.split("\\.").length == 3);
        
        // Verify both tokens are valid and contain correct usernames
        assertEquals(username1, jwtUtil.getUsernameFromToken(token1));
        assertEquals(username2, jwtUtil.getUsernameFromToken(token2));
    }

    @Test
    @DisplayName("Should handle token extraction with different secret keys")
    void getUsernameFromToken_DifferentSecretKeys() {
        // Arrange
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // Change secret key
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "differentSecretKey123456789");

        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwtUtil.getUsernameFromToken(token);
        });
    }

    @Test
    @DisplayName("Should handle token with missing claims")
    void validateToken_MissingClaims() {
        // Arrange
        String tokenWithMissingClaims = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.invalidsignature";

        // Act
        boolean isValid = jwtUtil.validateToken(tokenWithMissingClaims);

        // Assert
        assertFalse(isValid);
    }
} 