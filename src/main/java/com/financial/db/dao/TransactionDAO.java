package com.financial.db.dao;

import com.financial.db.FinancialDatabaseManager;
import com.financial.db.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO extends FinancialDatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(TransactionDAO.class);

    public TransactionDAO() {
        super();
    }

    public void createTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (account_number, amount, type, description, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, transaction.getAccountNumber());
            stmt.setBigDecimal(2, transaction.getAmount());
            stmt.setString(3, transaction.getType().name());
            stmt.setString(4, transaction.getDescription());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error creating transaction", e);
            throw e;
        }
    }

    public List<Transaction> getTransactionsByAccount(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY timestamp DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection connection = getConnection();
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
