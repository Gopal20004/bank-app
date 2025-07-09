# BankApp - Full Stack Banking Application

A modern banking application built with Spring Boot backend and React frontend, featuring JWT authentication, user management, and transaction handling.

## Features

### Backend (Spring Boot)
- **User Management**: Registration, login, and profile management
- **JWT Authentication**: Secure token-based authentication
- **Transaction Management**: Transfer funds, view transaction history
- **Balance Management**: Real-time balance updates
- **MySQL Database**: Persistent data storage
- **RESTful APIs**: Clean, documented API endpoints

### Frontend (React)
- **Modern UI**: Built with Tailwind CSS for a professional look
- **Responsive Design**: Works on desktop and mobile devices
- **Authentication**: Login/register pages with form validation
- **Dashboard**: Overview of account balance and quick actions
- **Protected Routes**: Secure navigation with JWT tokens

## Tech Stack

### Backend
- Spring Boot 3.2.0
- Spring Security with JWT
- Spring Data JPA
- MySQL Database
- Maven

### Frontend
- React 18
- React Router DOM
- Tailwind CSS
- Axios for API calls
- Local Storage for token management

## Prerequisites

- Java 17 or higher
- Node.js 16 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd bank-app
```

### 2. Backend Setup

#### Database Configuration
1. Create a MySQL database named `bankapp`
2. Update `src/main/resources/application.properties` with your database credentials:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

#### Run the Backend
```bash
# Navigate to backend directory
cd src/main/java/com/bankapp

# Build and run with Maven
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 3. Frontend Setup

#### Install Dependencies
```bash
# Install Node.js dependencies
npm install
```

#### Run the Frontend
```bash
npm start
```

The frontend will start on `http://localhost:3000`

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### User Management
- `GET /api/user/balance` - Get user balance
- `GET /api/user/profile` - Get user profile

### Transactions
- `GET /api/transactions` - Get transaction history
- `POST /api/transactions/transfer` - Transfer funds
- `POST /api/transactions/deposit` - Deposit money
- `POST /api/transactions/withdraw` - Withdraw money

## Usage

### 1. Registration
1. Navigate to `http://localhost:3000/register`
2. Fill in your details (username, email, password, full name)
3. Click "Create account"

### 2. Login
1. Navigate to `http://localhost:3000/login`
2. Enter your username and password
3. Click "Sign in"

### 3. Dashboard
After successful login, you'll be redirected to the dashboard where you can:
- View your current balance
- Access quick actions (Transfer, Transactions, Profile, Support)

## Project Structure

```
bank-app/
├── src/
│   ├── main/
│   │   ├── java/com/bankapp/
│   │   │   ├── config/          # Security configuration
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/            # Data transfer objects
│   │   │   ├── entity/         # JPA entities
│   │   │   ├── repository/     # Data repositories
│   │   │   ├── security/       # JWT utilities
│   │   │   ├── service/        # Business logic
│   │   │   └── BankAppApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── components/             # React components
│       ├── LoginPage.jsx
│       ├── RegisterPage.jsx
│       ├── Dashboard.jsx
│       └── ProtectedRoute.jsx
├── public/
├── package.json
├── tailwind.config.js
└── README.md
```

## Security Features

- **JWT Authentication**: Secure token-based authentication
- **Password Encryption**: BCrypt password hashing
- **Protected Routes**: Frontend route protection
- **CORS Configuration**: Cross-origin resource sharing setup
- **Input Validation**: Server-side validation for all inputs

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support, email support@bankapp.com or create an issue in the repository. 