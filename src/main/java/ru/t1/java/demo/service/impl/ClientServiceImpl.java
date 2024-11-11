package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.kafka.ClientProducer;
import ru.t1.java.demo.model.dto.CheckResponse;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.exception.EntityNotFoundException;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.mapper.ClientMapper;
import ru.t1.java.demo.web.CheckWebClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("ClientServiceImpl")
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final ClientProducer clientProducer;
        private final CheckWebClient checkWebClient;

    @PostConstruct
    void init() {
        List<Client> clients = new ArrayList<>();
        try {
            clients = parseJson();
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
        if (!clients.isEmpty())
            repository.saveAll(clients);
    }

    @Override
//    @LogExecution
//    @Track
//    @HandlingResult
    public List<Client> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ClientDto[] clients = mapper.readValue(new File("src/main/resources/MOCK_DATA.json"), ClientDto[].class);

        return Arrays.stream(clients)
                .map(ClientMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    @LogDataSourceError
    public Client getClient(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("%s with id = %d not found", "Client", id)));
    }

    @Override
    public List<Client> registerClients(List<Client> clients) {
        List<Client> savedClients = new ArrayList<>();
        for (Client client : clients) {
            Optional<CheckResponse> check = checkWebClient.check(client.getId());
            check.ifPresent(checkResponse -> {
                if (!checkResponse.getBlocked()) {
                    Client saved = repository.save(client);
                    clientProducer.send(saved.getId());
                    savedClients.add(saved);
                }
            });
//            savedClients.add(repository.save(client));
        }

        return savedClients;
    }

    @Override
    public Client registerClient(Client client) {
        /*Client saved = null;
        Optional<CheckResponse> check = checkWebClient.check(client.getId());
        if (check.isPresent()) {
            if (!check.get().getBlocked()) {
                saved = repository.save(client);
                kafkaClientProducer.send(client.getId());
            }
        }*/
        Client saved = repository.save(client);
        clientProducer.send(client.getId());
        return saved;
    }
    @Override
    public void clearMiddleName(List<ClientDto> dtos) {
        log.info("Clearing middle name");
        dtos.forEach(dto -> dto.setMiddleName(null));
        log.info("Done clearing middle name");
    }

}
