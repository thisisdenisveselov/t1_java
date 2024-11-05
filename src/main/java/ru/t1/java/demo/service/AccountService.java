package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;

import java.io.IOException;
import java.util.List;

public interface AccountService {
    List<Account> parseJson() throws IOException;
    List<Account> getAccounts();
    Account getAccount(Long id);
    Account createAccount(Account account);
    Account updateAccount(Long accountId, Account account);
    void deleteAccount(Long id);
}
