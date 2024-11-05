package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Transaction;

import java.io.IOException;
import java.util.List;

public interface TransactionService {
    List<Transaction> parseJson() throws IOException;
    List<Transaction> getTransactions();
    Transaction getTransaction(Long id);
    Transaction createTransaction(Transaction transaction);
    Transaction updateTransaction(Long transactionId, Transaction transaction);
    void deleteTransaction(Long id);
}
