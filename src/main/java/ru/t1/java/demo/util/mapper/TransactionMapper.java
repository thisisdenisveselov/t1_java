package ru.t1.java.demo.util.mapper;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;

@Component
public class TransactionMapper {
    public static Transaction toEntity(TransactionDto dto) {
        return Transaction.builder()
                .id(dto.getId())
                .amount(dto.getAmount())
                .createdAt(dto.getCreatedAt())
                .account(Account.builder()
                        .id(dto.getAccount().getId()).build())
                .build();
    }

    public static TransactionDto toDto(Transaction entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .createdAt(entity.getCreatedAt())
                .account(AccountMapper.toDto(entity.getAccount()))
                .build();
    }

}
