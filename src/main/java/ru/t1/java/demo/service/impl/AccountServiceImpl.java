package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.exception.EntityNotFoundException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.mapper.AccountMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("AccountServiceImpl")
@Slf4j
@RequiredArgsConstructor
@DependsOn("ClientServiceImpl")
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final ClientService clientService;

    @PostConstruct
    void init() {
        List<Account> accounts = new ArrayList<>();
        try {
            accounts = parseJson();
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
        if (!accounts.isEmpty()) {
            accounts.forEach(account ->
                    account.setOwner(clientService.getClient(account.getOwner().getId())));
            accountRepository.saveAll(accounts);
        }
    }

    @Override
//    @LogExecution
//    @Track
//    @HandlingResult
    public List<Account> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        AccountDto[] accounts = mapper
                .readValue(new File("src/main/resources/MOCK_DATA_ACCOUNTS.json"), AccountDto[].class);

        return Arrays.stream(accounts)
                .map(AccountMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @Override
    @LogDataSourceError
    public Account getAccount(Long id) {
        return accountRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException(String.format("%s with id = %d not found", "Account", id)));
    }

    @Override
    @LogDataSourceError
    public Account createAccount(Account account) {
        Client owner = clientService.getClient(account.getOwner().getId());
        account.setOwner(owner);
        return accountRepository.save(account);
    }

    @Override
    @LogDataSourceError
    public Account updateAccount(Long accountId, Account account) {
        Client owner = clientService.getClient(account.getOwner().getId());
        account.setId(accountId);
        account.setOwner(owner);
        return accountRepository.save(account);
    }

    @Override
    @LogDataSourceError
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}
