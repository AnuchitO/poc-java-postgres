package com.financial.db.dao;

import com.financial.db.FinancialDatabaseManager;
import com.financial.db.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AccountDAO {
    private static final Logger logger = LoggerFactory.getLogger(AccountDAO.class);
    private final FinancialDatabaseManager dbManager;

    public AccountDAO(FinancialDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void createAccount(Account account) {
        String sql = "INSERT INTO accounts (account_number, balance, owner_name, created_at, type) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = dbManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, account.getAccountNumber());
            stmt.setBigDecimal(2, account.getBalance());
            stmt.setString(3, account.getOwnerName());
            stmt.setTimestamp(4, java.sql.Timestamp.valueOf(account.getCreatedAt()));
            stmt.setString(5, account.getType().name());
            
            stmt.executeUpdate();
            logger.info("Account created successfully");
        } catch (SQLException e) {
            logger.error("Failed to create account", e);
            throw new RuntimeException("Failed to create account", e);
        }
    }

    public Account getAccountByNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        
        try (Connection connection = dbManager.getConnection();
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
