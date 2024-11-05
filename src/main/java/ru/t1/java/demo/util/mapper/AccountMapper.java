package ru.t1.java.demo.util.mapper;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;

@Component
public class AccountMapper {
    public static Account toEntity(AccountDto dto) {
        return Account.builder()
                .id(dto.getId())
                .type(dto.getType())
                .balance(dto.getBalance())
                .owner(ClientMapper.toEntity(dto.getOwner()))
                .build();
    }

    public static AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .id(entity.getId())
                .type(entity.getType())
                .balance(entity.getBalance())
                .owner(ClientMapper.toDto(entity.getOwner()))
                .build();
    }

}
