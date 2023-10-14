package pl.edu.pw.mwotest.clients;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Client createClient(Client newClient) {
        Set<ConstraintViolation<Client>> violations = validator.validate(newClient);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return repository.save(newClient);
    }

    public void deleteClient(int id) {
        repository.deleteById(id);
    }

    public Client updateClient(int id, Client updatedClient) {
        Set<ConstraintViolation<Client>> violations = validator.validate(updatedClient);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        Client clientToUpdate = repository.findById(id).orElse(null);

        if (clientToUpdate == null) {
            throw new IllegalArgumentException(String.format("The client with ID %d was not found - failed to update.", id));
        }

        clientToUpdate.setName(updatedClient.getName());
        clientToUpdate.setSurname(updatedClient.getSurname());
        clientToUpdate.setEmail(updatedClient.getEmail());

        return repository.save(clientToUpdate);
    }
}
