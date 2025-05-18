package com.financial.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public Connection getConnection() {
        return connection;
    }
}