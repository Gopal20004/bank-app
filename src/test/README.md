# BankApp Test Suite

This directory contains comprehensive unit tests for the BankApp Spring Boot application using JUnit 5 and Mockito.

## Test Structure

```
src/test/java/com/bankapp/
├── BankAppTestConfig.java                    # Test configuration
├── security/
│   └── JwtUtilTest.java                     # JWT utility tests
└── service/
    └── impl/
        ├── AuthServiceImplTest.java          # Authentication service tests
        ├── TransactionServiceImplTest.java   # Transaction service tests
        └── UserServiceImplTest.java          # User service tests
```

## Test Coverage

### 1. TransactionServiceImplTest
**File**: `src/test/java/com/bankapp/service/impl/TransactionServiceImplTest.java`

**Coverage**: Tests the core transfer functionality and transaction management.

**Key Test Cases**:
- ✅ **Successful fund transfers** between users
- ✅ **Insufficient balance** validation
- ✅ **Self-transfer prevention** (users cannot transfer to themselves)
- ✅ **Recipient not found** scenarios
- ✅ **Deposit and withdrawal** operations
- ✅ **Transaction history** retrieval
- ✅ **Paginated transaction** queries
- ✅ **Date range filtering** for transactions
- ✅ **Transaction property** validation
- ✅ **Edge cases** (zero amounts, null descriptions, large amounts)

**Mocked Dependencies**:
- `TransactionRepository`
- `UserService`

### 2. UserServiceImplTest
**File**: `src/test/java/com/bankapp/service/impl/UserServiceImplTest.java`

**Coverage**: Tests user management and registration functionality.

**Key Test Cases**:
- ✅ **User registration** with validation
- ✅ **Duplicate username/email** prevention
- ✅ **User lookup** by various criteria (ID, username, account number)
- ✅ **Balance management** operations
- ✅ **User existence** checks
- ✅ **Error handling** for missing users
- ✅ **Edge cases** (null values, negative balances)

**Mocked Dependencies**:
- `UserRepository`
- `PasswordEncoder`

### 3. AuthServiceImplTest
**File**: `src/test/java/com/bankapp/service/impl/AuthServiceImplTest.java`

**Coverage**: Tests authentication and JWT token management.

**Key Test Cases**:
- ✅ **User login** with authentication
- ✅ **JWT token generation** and validation
- ✅ **User registration** through auth service
- ✅ **Authentication failure** handling
- ✅ **Token extraction** and validation
- ✅ **Error scenarios** (invalid credentials, missing users)
- ✅ **Edge cases** (null/empty tokens, authentication failures)

**Mocked Dependencies**:
- `UserService`
- `JwtUtil`
- `AuthenticationManager`

### 4. JwtUtilTest
**File**: `src/test/java/com/bankapp/security/JwtUtilTest.java`

**Coverage**: Tests JWT token generation, validation, and parsing.

**Key Test Cases**:
- ✅ **Token generation** for different usernames
- ✅ **Token validation** for valid/invalid tokens
- ✅ **Username extraction** from tokens
- ✅ **Expired token** handling
- ✅ **Malformed token** rejection
- ✅ **Signature validation**
- ✅ **Edge cases** (null/empty tokens, special characters, long usernames)

## Running the Tests

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Spring Boot 3.x

### Command Line

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=TransactionServiceImplTest

# Run tests with coverage report
mvn test jacoco:report

# Run tests in verbose mode
mvn test -X

# Run tests and skip integration tests
mvn test -DskipITs=true
```

### IDE Integration

**IntelliJ IDEA**:
1. Right-click on `src/test/java` folder
2. Select "Run 'All Tests'"
3. Or right-click on individual test classes to run specific tests

**Eclipse**:
1. Right-click on test class
2. Select "Run As" → "JUnit Test"

**VS Code**:
1. Install Java Test Runner extension
2. Use the test explorer to run individual or all tests

## Test Configuration

### BankAppTestConfig
**File**: `src/test/java/com/bankapp/BankAppTestConfig.java`

Provides test-specific configuration:
- Password encoder bean for testing
- Test-specific beans and configurations

## Test Dependencies

The tests use the following dependencies (already included in `pom.xml`):

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

## Test Best Practices

### 1. Test Naming Convention
- Use descriptive test method names: `methodName_Scenario_ExpectedResult`
- Example: `transferFunds_InsufficientBalance_ThrowsException`

### 2. Test Structure (AAA Pattern)
```java
@Test
@DisplayName("Should successfully transfer funds between users")
void transferFunds_Success() {
    // Arrange - Setup test data and mocks
    when(userService.findById(1L)).thenReturn(sender);
    
    // Act - Execute the method under test
    Transaction result = transactionService.transferFunds(1L, transferDto);
    
    // Assert - Verify the results
    assertNotNull(result);
    verify(userService).updateBalance(1L, new BigDecimal("900.00"));
}
```

### 3. Mocking Guidelines
- Mock external dependencies (repositories, services)
- Use `@Mock` and `@InjectMocks` annotations
- Verify mock interactions when relevant
- Use argument matchers for flexible matching

### 4. Assertion Best Practices
- Use specific assertions (`assertEquals`, `assertNotNull`)
- Verify both return values and side effects
- Test both positive and negative scenarios
- Include edge cases and error conditions

## Test Data Management

### Test Fixtures
Each test class includes `@BeforeEach` setup methods that create:
- Mock user objects with realistic data
- DTO objects for testing
- Sample transaction data

### Test Data Examples
```java
private User sender = new User();
sender.setId(1L);
sender.setUsername("sender");
sender.setAccountNumber("ACC001");
sender.setBalance(new BigDecimal("1000.00"));
```

## Coverage Goals

The test suite aims for:
- **Line Coverage**: > 90%
- **Branch Coverage**: > 85%
- **Method Coverage**: > 95%

## Continuous Integration

Tests are automatically run in CI/CD pipelines:
- On every pull request
- Before merging to main branch
- As part of the build process

## Troubleshooting

### Common Issues

1. **Test Failures Due to Missing Beans**
   - Ensure `BankAppTestConfig` is properly configured
   - Check that all required dependencies are mocked

2. **JWT Token Issues**
   - Verify test secret key is properly set
   - Check token expiration settings

3. **Database Connection Issues**
   - Tests use in-memory H2 database or mocks
   - No external database connection required

### Debug Mode
Run tests with debug logging:
```bash
mvn test -Dspring.profiles.active=test -Dlogging.level.com.bankapp=DEBUG
```

## Contributing

When adding new features:
1. Write tests first (TDD approach)
2. Ensure all tests pass
3. Maintain test coverage above thresholds
4. Update this README with new test information

## Test Reports

After running tests, reports are generated in:
- **Surefire Reports**: `target/surefire-reports/`
- **Coverage Reports**: `target/site/jacoco/` (if using JaCoCo)
- **Test Results**: IDE test runners

---

For more information about testing in Spring Boot, refer to the [Spring Boot Testing Guide](https://spring.io/guides/gs/testing-web/). 