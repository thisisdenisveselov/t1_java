package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.ClientDto;

import java.io.IOException;
import java.util.List;

public interface ClientService {
    List<Client> parseJson() throws IOException;
    Client getClient(Long id);
    List<Client> registerClients(List<Client> clients);

    Client registerClient(Client client);

    void clearMiddleName(List<ClientDto> dtos);
}
