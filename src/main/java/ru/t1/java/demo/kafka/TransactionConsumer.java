package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.mapper.AccountMapper;
import ru.t1.java.demo.util.mapper.TransactionMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionConsumer {

    private final TransactionService transactionService;

    @KafkaListener(groupId = "${t1.kafka.consumer.transaction-id}",
            topics = "${t1.kafka.topic.transactions}",
            containerFactory = "transactionKafkaListenerContainerFactory")
    public void listener(TransactionDto transactionDto, Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Transaction consumer: new message from {} topic", topic);
        Transaction transaction = TransactionMapper.toEntity(transactionDto);

        try {
            transactionService.createTransaction(transaction);
        } catch (Throwable throwable) {
            log.error("Transaction consumer: Error while saving new transaction: {}", transaction.toString(), throwable);
        } finally {
            ack.acknowledge();
        }

        log.debug("Transaction consumer: message processed");
    }
}
