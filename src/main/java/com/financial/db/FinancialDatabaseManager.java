package com.financial.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.financial.db.model.Account;
import com.financial.db.model.Transaction;
import com.financial.db.model.AccountTransactionPair;

public class FinancialDatabaseManager implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(FinancialDatabaseManager.class);
    private static final String DB_HOST = System.getenv("DB_HOST");
    private static final String DB_PORT = System.getenv("DB_PORT");
    private static final String DB_NAME = System.getenv("DB_NAME");
    private static final String DB_URL = "jdbc:postgresql://" + 
        (DB_HOST != null ? DB_HOST : "localhost") + ":" + 
        (DB_PORT != null ? DB_PORT : "5432") + "/" + 
        (DB_NAME != null ? DB_NAME : "financial_db");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASS = System.getenv("DB_PASS");

    private Connection connection;

    public FinancialDatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            logger.info("Successfully connected to the database");
        } catch (SQLException e) {
            logger.error("Failed to connect to database", e);
            throw new RuntimeException("Database connection failed", e);
        }
    }

    /**
     * Create a new account
     * @param accountNumber Account number
     * @param balance Initial balance
     * @param ownerName Account owner name
     * @param type Account type (SAVINGS, CHECKING, CREDIT)
     */
    public void createAccount(String accountNumber, BigDecimal balance, String ownerName, String type) {
        String sql = "INSERT INTO accounts (account_number, balance, owner_name, created_at, type) VALUES (?, ?, ?, NOW(), ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.setBigDecimal(2, balance);
            stmt.setString(3, ownerName);
            stmt.setString(4, type);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error creating account", e);
            throw new RuntimeException("Failed to create account", e);
        }
    }

    /**
     * Create a new transaction
     * @param accountNumber Account number
     * @param amount Transaction amount
     * @param type Transaction type (DEPOSIT, WITHDRAWAL, TRANSFER)
     * @param description Transaction description
     */
    public void createTransaction(String accountNumber, BigDecimal amount, String type, String description) {
        String sql = "INSERT INTO transactions (account_number, amount, type, timestamp, description) VALUES (?, ?, ?, NOW(), ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.setBigDecimal(2, amount);
            stmt.setString(3, type);
            stmt.setString(4, description);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error creating transaction", e);
            throw new RuntimeException("Failed to create transaction", e);
        }
    }

    /**
     * Example of INNER JOIN between accounts and transactions tables
     * @return List of account-transaction pairs
     */
    public List<AccountTransactionPair> getAccountTransactions() {
        String sql = "SELECT a.id as account_id, a.account_number, a.balance, a.owner_name, " +
                    "t.id as transaction_id, t.amount, t.type, t.timestamp, t.description " +
                    "FROM accounts a " +
                    "INNER JOIN transactions t ON a.account_number = t.account_number " +
                    "ORDER BY a.id, t.timestamp DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            List<AccountTransactionPair> results = new ArrayList<>();
            
            while (rs.next()) {
                AccountTransactionPair pair = new AccountTransactionPair(
                    new Account(
                        rs.getLong("account_id"),
                        rs.getString("account_number"),
                        rs.getBigDecimal("balance"),
                        rs.getString("owner_name")
                    ),
                    new Transaction(
                        rs.getLong("transaction_id"),
                        rs.getString("account_number"),
                        rs.getBigDecimal("amount"),
                        Transaction.TransactionType.valueOf(rs.getString("type")),
                        rs.getTimestamp("timestamp").toLocalDateTime(),
                        rs.getString("description")
                    )
                );
                results.add(pair);
            }
            
            return results;
        } catch (SQLException e) {
            logger.error("Error executing query", e);
            throw new RuntimeException("Database query failed", e);
        }
    }

    /**
     * Example of LEFT JOIN to get all accounts with their latest transaction
     * @return Map of account number to latest transaction
     */
    public Map<String, Transaction> getLatestTransactions() {
        String sql = "SELECT a.account_number, " +
                    "t.id as transaction_id, t.amount, t.type, t.timestamp, t.description " +
                    "FROM accounts a " +
                    "LEFT JOIN transactions t ON a.account_number = t.account_number " +
                    "WHERE t.timestamp = (" +
                    "    SELECT MAX(t2.timestamp) " +
                    "    FROM transactions t2 " +
                    "    WHERE t2.account_number = a.account_number) " +
                    "ORDER BY a.account_number";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            Map<String, Transaction> results = new HashMap<>();
            
            while (rs.next()) {
                String accountNumber = rs.getString("account_number");
                Transaction transaction = new Transaction(
                    rs.getLong("transaction_id"),
                    accountNumber,
                    rs.getBigDecimal("amount"),
                    Transaction.TransactionType.valueOf(rs.getString("type")),
                    rs.getTimestamp("timestamp").toLocalDateTime(),
                    rs.getString("description")
                );
                results.put(accountNumber, transaction);
            }
            
            return results;
        } catch (SQLException e) {
            logger.error("Error executing query", e);
            throw new RuntimeException("Database query failed", e);
        }
    }

    /**
     * Example of GROUP BY with aggregate functions
     * @return Map of account number to total transaction amount
     */
    public Map<String, BigDecimal> getAccountBalances() {
        String sql = "SELECT t.account_number, SUM(t.amount) as total_amount " +
                    "FROM transactions t " +
                    "GROUP BY t.account_number";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            Map<String, BigDecimal> results = new HashMap<>();
            
            while (rs.next()) {
                String accountNumber = rs.getString("account_number");
                BigDecimal totalAmount = rs.getBigDecimal("total_amount");
                results.put(accountNumber, totalAmount);
            }
            
            return results;
        } catch (SQLException e) {
            logger.error("Error executing query", e);
            throw new RuntimeException("Database query failed", e);
        }
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed");
            } catch (SQLException e) {
                logger.error("Failed to close database connection", e);
            }
        }
    }

    protected Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                logger.info("Successfully connected to the database");
            } catch (SQLException e) {
                logger.error("Failed to connect to database", e);
                throw new RuntimeException("Database connection failed", e);
            }
        }
        return connection;
    }
}