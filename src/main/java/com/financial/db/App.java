package com.financial.db;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.time.LocalDateTime;

public class App {
    public static void main(String[] args) {
        try (FinancialDatabaseManager manager = new FinancialDatabaseManager()) {
            System.out.println("Financial Database Manager is running!");
            
            // Test database connection
            try (Connection conn = manager.getConnection()) {
                // Create a test account
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("INSERT INTO accounts (account_number, balance, owner_name, created_at, type) VALUES ('1234567890', 1000.00, 'Test User', NOW(), 'SAVINGS')");
                    System.out.println("Test account created successfully");
                }

                // Query the account
                try (Statement stmt = conn.createStatement()) {
                    ResultSet rs = stmt.executeQuery("SELECT * FROM accounts WHERE account_number = '1234567890'");
                    if (rs.next()) {
                        System.out.println("Account found:");
                        System.out.println("Account Number: " + rs.getString("account_number"));
                        System.out.println("Balance: " + rs.getDouble("balance"));
                        System.out.println("Owner: " + rs.getString("owner_name"));
                        System.out.println("Type: " + rs.getString("type"));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
