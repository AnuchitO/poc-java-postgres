package com.financial.db.dao;

import com.financial.db.FinancialDatabaseManager;
import com.financial.db.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private static final Logger logger = LoggerFactory.getLogger(TransactionDAO.class);
    private final FinancialDatabaseManager dbManager;

    public TransactionDAO(FinancialDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void createTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (account_number, amount, type, timestamp, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = dbManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, transaction.getAccountNumber());
            stmt.setBigDecimal(2, transaction.getAmount());
            stmt.setString(3, transaction.getType().name());
            stmt.setTimestamp(4, java.sql.Timestamp.valueOf(transaction.getTimestamp()));
            stmt.setString(5, transaction.getDescription());
            
            stmt.executeUpdate();
            logger.info("Transaction created successfully");
        } catch (SQLException e) {
            logger.error("Failed to create transaction", e);
            throw new RuntimeException("Failed to create transaction", e);
        }
    }

    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY timestamp DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getLong("id"));
                transaction.setAccountNumber(rs.getString("account_number"));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setType(Transaction.TransactionType.valueOf(rs.getString("type")));
                transaction.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                transaction.setDescription(rs.getString("description"));
                
                transactions.add(transaction);
            }
            
        } catch (SQLException e) {
            logger.error("Failed to fetch transactions", e);
            throw new RuntimeException("Failed to fetch transactions", e);
        }
        
        return transactions;
    }
}
