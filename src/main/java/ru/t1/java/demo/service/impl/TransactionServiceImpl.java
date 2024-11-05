package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.EntityNotFoundException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.mapper.TransactionMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@DependsOn("AccountServiceImpl")
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @PostConstruct
    void init() {
        List<Transaction> transactions = new ArrayList<>();
        try {
            transactions = parseJson();
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
        if (!transactions.isEmpty()) {
            transactions.forEach(transaction ->
                    transaction.setAccount(accountService.getAccount(transaction.getAccount().getId())));
            transactionRepository.saveAll(transactions);
        }
    }

    @Override
//    @LogExecution
//    @Track
//    @HandlingResult
    public List<Transaction> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);

        TransactionDto[] transactions = mapper
                .readValue(new File("src/main/resources/MOCK_DATA_TRANSACTIONS.json"), TransactionDto[].class);

        return Arrays.stream(transactions)
                .map(TransactionMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    @LogDataSourceError
    public Transaction getTransaction(Long id) {
        return transactionRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException(String.format("%s with id = %d not found", "Transaction", id)));
    }

    @Override
    @LogDataSourceError
    public Transaction createTransaction(Transaction transaction) {
        Account account = accountService.getAccount(transaction.getAccount().getId());
        transaction.setAccount(account);
        return transactionRepository.save(transaction);
    }

    @Override
    @LogDataSourceError
    public Transaction updateTransaction(Long transactionId, Transaction transaction) {
        Account account = accountService.getAccount(transaction.getAccount().getId());
        transaction.setId(transactionId);
        transaction.setAccount(account);
        return transactionRepository.save(transaction);
    }

    @Override
    @LogDataSourceError
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}
