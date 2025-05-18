package com.financial.db.model;

public class AccountTransactionPair {
    private final Account account;
    private final Transaction transaction;

    public AccountTransactionPair(Account account, Transaction transaction) {
        this.account = account;
        this.transaction = transaction;
    }

    public Account getAccount() {
        return account;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    @Override
    public String toString() {
        return "AccountTransactionPair{" +
                "account=" + account +
                ", transaction=" + transaction +
                '}';
    }
}
