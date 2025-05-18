package com.financial.db.dao;

import com.financial.db.FinancialDatabaseManager;
import com.financial.db.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class AccountDAO extends FinancialDatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(AccountDAO.class);

    public AccountDAO() {
        super();
    }

    public void createAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (account_number, balance, owner_name, type, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, account.getAccountNumber());
            stmt.setBigDecimal(2, account.getBalance());
            stmt.setString(3, account.getOwnerName());
            stmt.setString(4, account.getType().name());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error creating account", e);
            throw e;
        }
    }

    public Account getAccountByNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Account account = new Account();
                account.setId(rs.getLong("id"));
                account.setAccountNumber(rs.getString("account_number"));
                account.setBalance(rs.getBigDecimal("balance"));
                account.setOwnerName(rs.getString("owner_name"));
                account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                account.setType(Account.AccountType.valueOf(rs.getString("type")));
                
                return account;
            }
            
        } catch (SQLException e) {
            logger.error("Failed to fetch account", e);
            throw new RuntimeException("Failed to fetch account", e);
        }
        
        return null;
    }
}
