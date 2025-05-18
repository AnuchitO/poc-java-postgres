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

## Usage

The project provides DAO classes for interacting with the database:
- `AccountDAO`: For managing accounts
- `TransactionDAO`: For managing transactions

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request
