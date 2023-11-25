package pl.edu.pw.mwotest.services;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pw.mwotest.dtos.ClientDto;
import pl.edu.pw.mwotest.models.Client;
import pl.edu.pw.mwotest.repositories.ClientRepository;

import java.util.ArrayList;
import java.util.Set;

@Service

public class ClientService {
    private final ClientRepository repository;
    private final Validator validator;

    @Autowired
    public ClientService(ClientRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public Client getClient(int id) {
        return repository.findById(id).orElse(null);
    }

    public Iterable<Client> getAllClients() {
        return repository.findAll();
    }

    public Client createClient(Client client) {
        Set<ConstraintViolation<Client>> violations = validator.validate(client);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return repository.save(client);
    }

    public void deleteClient(int id) {
        repository.deleteById(id);
    }

    public Client updateClient(int id, Client client) {
        Client clientToUpdate = repository.findById(id).orElse(null);

        if (clientToUpdate == null) {
            throw new IllegalArgumentException(String.format("The client with ID %d was not found - failed to update.", id));
        }

        clientToUpdate.setName(client.getName());
        clientToUpdate.setSurname(client.getSurname());
        clientToUpdate.setEmail(client.getEmail());

        Set<ConstraintViolation<Client>> violations = validator.validate(clientToUpdate);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return repository.save(clientToUpdate);
    }

    public Client mapFromDto(ClientDto dto) {
        return dto != null ? new Client(
                -1,
                dto.getName(),
                dto.getSurname(),
                dto.getEmail(),
                new ArrayList<>()
        ) : null;
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}
