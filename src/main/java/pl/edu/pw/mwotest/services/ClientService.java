package pl.edu.pw.mwotest.services;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pw.mwotest.models.Client;
import pl.edu.pw.mwotest.repositories.ClientRepository;

@Service

public class ClientService {
    private final ClientRepository repository;

    @Autowired
    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public Client getClient(int id) {
        return repository.findById(id).orElse(null);
    }

    public Iterable<Client> getAllClients() {
        return repository.findAll();
    }

    public Client createClient(@Valid Client client) {
        return repository.save(client);
    }

    public void deleteClient(int id) {
        repository.deleteById(id);
    }

    public Client updateClient(int id, @Valid Client client) {
        Client clientToUpdate = repository.findById(id).orElse(null);

        if (clientToUpdate == null) {
            throw new IllegalArgumentException(String.format("The client with ID %d was not found - failed to update.", id));
        }

        clientToUpdate.setName(client.getName());
        clientToUpdate.setSurname(client.getSurname());
        clientToUpdate.setEmail(client.getEmail());

        return repository.save(clientToUpdate);
    }
}
