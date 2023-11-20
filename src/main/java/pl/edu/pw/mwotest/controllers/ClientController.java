package pl.edu.pw.mwotest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.mwotest.dtos.ClientDto;
import pl.edu.pw.mwotest.models.Client;
import pl.edu.pw.mwotest.services.ClientService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ClientController {
    private final ClientService service;

    @Autowired
    public ClientController(ClientService service) {
        this.service = service;
    }

    @PostMapping("/clients")
    public Client create(@RequestBody ClientDto clientDto) {
        return service.createClient(service.mapFromDto(clientDto));
    }

    @GetMapping("/clients")
    public List<Client> getAll() {
        List<Client> clients = new ArrayList<>();

        service.getAllClients().forEach(clients::add);

        return clients;
    }

    @GetMapping("/clients/{id}")
    public Client get(@PathVariable int id) {
        return service.getClient(id);
    }

    @PatchMapping("/clients/{id}")
    public Client update(@PathVariable int id, @RequestBody ClientDto clientDto) {
        return service.updateClient(id, service.mapFromDto(clientDto));
    }

    @DeleteMapping("/clients/{id}")
    public void delete(@PathVariable int id) {
        service.deleteClient(id);
    }
}
