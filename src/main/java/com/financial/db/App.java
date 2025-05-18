package com.financial.db;

import java.sql.Connection;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.financial.db.dao.AccountDAO;
import com.financial.db.dao.TransactionDAO;
import com.financial.db.model.Account;
import com.financial.db.model.Transaction;
import com.financial.db.model.AccountTransactionPair;
import com.financial.db.FinancialDatabaseManager;

public class App {
    public static void main(String[] args) {
        try {
            FinancialDatabaseManager manager = new FinancialDatabaseManager();
            System.out.println("Financial Database Manager is running!");
            
            // Create DAO instances
            AccountDAO accountDAO = new AccountDAO();
            TransactionDAO transactionDAO = new TransactionDAO();
            
            // Create test data
            try {
                // Create test account
                Account account = new Account();
                account.setAccountNumber("1234567890");
                account.setBalance(new BigDecimal("1000.00"));
                account.setOwnerName("Test User");
                account.setType(Account.AccountType.SAVINGS);
                accountDAO.createAccount(account);
                
                // Create test transactions
                Transaction transaction1 = new Transaction();
                transaction1.setAccountNumber("1234567890");
                transaction1.setAmount(new BigDecimal("500.00"));
                transaction1.setType(Transaction.TransactionType.DEPOSIT);
                transaction1.setDescription("Initial deposit");
                transactionDAO.createTransaction(transaction1);
                
                Transaction transaction2 = new Transaction();
                transaction2.setAccountNumber("1234567890");
                transaction2.setAmount(new BigDecimal("250.00"));
                transaction2.setType(Transaction.TransactionType.WITHDRAWAL);
                transaction2.setDescription("Groceries");
                transactionDAO.createTransaction(transaction2);
                
                System.out.println("Test data created successfully");
            } catch (Exception e) {
                System.err.println("Error creating test data: " + e.getMessage());
                throw e;
            }

            // Demonstrate INNER JOIN - Get account transactions
            List<AccountTransactionPair> accountTransactions = manager.getAccountTransactions();
            System.out.println("\nAccount Transactions:");
            for (AccountTransactionPair pair : accountTransactions) {
                System.out.println("Account: " + pair.getAccount().getAccountNumber() + 
                                ", Transaction: " + pair.getTransaction().getDescription() + 
                                ", Amount: " + pair.getTransaction().getAmount());
            }

            // Demonstrate LEFT JOIN - Get latest transactions
            Map<String, Transaction> latestTransactions = manager.getLatestTransactions();
            System.out.println("\nLatest Transactions:");
            for (String accountNumber : latestTransactions.keySet()) {
                Transaction transaction = latestTransactions.get(accountNumber);
                System.out.println("Account: " + accountNumber + 
                                ", Latest Transaction: " + transaction.getDescription() + 
                                ", Amount: " + transaction.getAmount());
            }

            // Demonstrate GROUP BY - Get account balances
            Map<String, BigDecimal> accountBalances = manager.getAccountBalances();
            System.out.println("\nAccount Balances:");
            for (String accountNumber : accountBalances.keySet()) {
                BigDecimal balance = accountBalances.get(accountNumber);
                System.out.println("Account: " + accountNumber + 
                                ", Balance: " + balance);
            }

            // Close the manager explicitly
            manager.close();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
