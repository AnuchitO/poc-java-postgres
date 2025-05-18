# Financial Database Manager

This Java project provides a financial domain database management system using PostgreSQL.

## Features

- Account management (create, read)
- Transaction handling (create, read by account)
- Support for different account types (Savings, Checking, Credit)
- Support for different transaction types (Deposit, Withdrawal, Transfer)

## Database Schema

### Accounts Table
```sql
CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    balance DECIMAL(15,2) NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    type VARCHAR(20) NOT NULL
);
```

### Transactions Table
```sql
CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    description TEXT,
    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
);
```

## Prerequisites

- Java 17 or higher
- PostgreSQL 14 or higher
- Gradle 7.0 or higher

## Setup

1. Clone the repository
2. Create a PostgreSQL database named `financial_db`
3. Update database connection details in `FinancialDatabaseManager.java`
4. Run the project using Gradle:
   ```bash
   ./gradlew clean build
   ./gradlew run
   ```

## Code Architecture and Database Connection

### Database Connection

The application uses PostgreSQL as the database system. The connection is managed through the `FinancialDatabaseManager` class, which handles:
1. Database connection pooling
2. Connection lifecycle management
3. SQL query execution
4. Error handling and logging

The connection details are loaded from environment variables:
- `DB_HOST`: Database host (default: localhost)
- `DB_PORT`: Database port (default: 5432)
- `DB_NAME`: Database name (default: financial_db)
- `DB_USER`: Database username
- `DB_PASS`: Database password

### Core Components

1. **FinancialDatabaseManager.java**
   - Manages database connections
   - Provides CRUD operations for accounts and transactions
   - Implements SQL queries for joins and aggregations
   - Handles transaction management

2. **AccountDAO.java**
   - Manages account-related operations
   - Implements account creation, retrieval, and updates
   - Uses INNER JOIN to fetch account transactions
   - Handles account balance calculations

3. **TransactionDAO.java**
   - Manages transaction-related operations
   - Implements transaction creation and retrieval
   - Uses LEFT JOIN to fetch latest transactions
   - Handles transaction type and amount management

### Data Models

The application uses the following models:

1. **Account**
   - Properties: account_number, balance, owner_name, type, created_at
   - Types: SAVINGS, CHECKING
   - Stores customer account information

2. **Transaction**
   - Properties: account_number, amount, type, description, timestamp
   - Types: DEPOSIT, WITHDRAWAL, TRANSFER
   - Records all account transactions

### Key Operations

1. **Account Operations**
   ```java
   // Create a new account
   Account account = new Account();
   account.setAccountNumber("1234567890");
   account.setBalance(new BigDecimal("1000.00"));
   account.setOwnerName("Test User");
   account.setType(Account.AccountType.SAVINGS);
   accountDAO.createAccount(account);
   ```

2. **Deposit Operations**
   ```java
   // Deposit money to an account
   BigDecimal depositAmount = new BigDecimal("500.00");
   manager.deposit("1234567890", depositAmount, "Salary deposit");
   ```

3. **Withdraw Operations**
   ```java
   // Withdraw money from an account
   BigDecimal withdrawAmount = new BigDecimal("250.00");
   manager.withdraw("1234567890", withdrawAmount, "Groceries");
   ```

4. **Transaction Operations**
   ```java
   // Create a new transaction
   Transaction transaction = new Transaction();
   transaction.setAccountNumber("1234567890");
   transaction.setAmount(new BigDecimal("500.00"));
   transaction.setType(Transaction.TransactionType.DEPOSIT);
   transaction.setDescription("Initial deposit");
   transactionDAO.createTransaction(transaction);
   ```

5. **Database Queries**
   - INNER JOIN: Retrieves account transactions
   - LEFT JOIN: Gets latest transactions per account
   - GROUP BY: Calculates account balances

### Account Operations Details

1. **Deposit Process**
   - Updates account balance by adding the deposit amount
   - Creates a transaction record with type "DEPOSIT"
   - Uses database transaction to ensure atomicity
   - Logs all operations for audit trail

2. **Withdraw Process**
   - Checks if account exists
   - Verifies sufficient balance
   - Updates account balance by subtracting the amount
   - Creates a transaction record with negative amount
   - Uses database transaction to ensure atomicity
   - Logs all operations and errors

3. **Error Handling**
   - Insufficient funds check
   - Account existence verification
   - Database transaction rollback on error
   - Detailed error logging

### Usage

1. **Running the Application**
   ```bash
   # Build the project
   ./gradlew clean build

   # Run the application
   ./gradlew run
   ```

2. **Database Setup**
   ```sql
   -- Create database
   CREATE DATABASE financial_db;

   -- Create tables
   CREATE TABLE accounts (
       account_number VARCHAR(20) PRIMARY KEY,
       balance DECIMAL(15,2),
       owner_name VARCHAR(100),
       type VARCHAR(20),
       created_at TIMESTAMP
   );

   CREATE TABLE transactions (
       id SERIAL PRIMARY KEY,
       account_number VARCHAR(20),
       amount DECIMAL(15,2),
       type VARCHAR(20),
       description VARCHAR(200),
       timestamp TIMESTAMP
   );
   ```

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request
