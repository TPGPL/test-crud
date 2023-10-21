package pl.edu.pw.mwotest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.mwotest.dtos.ClientDto;
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

    @PostMapping("/clients/create")
    public ClientDto create(@RequestBody ClientDto clientDto) {
        return ClientDto.mapToDto(service.createClient(service.mapFromDto(clientDto)));
    }

    @GetMapping("/clients")
    public List<ClientDto> getAll() {
        List<ClientDto> clients = new ArrayList<>();

        service.getAllClients().forEach((x) -> clients.add(ClientDto.mapToDto(x)));

        return clients;
    }

    @GetMapping("/clients/{id}")
    public ClientDto get(@PathVariable int id) {
        return ClientDto.mapToDto(service.getClient(id));
    }

    @PatchMapping("/clients/{id}")
    public ClientDto update(@PathVariable int id, @RequestBody ClientDto clientDto) {
        return ClientDto.mapToDto(service.updateClient(id, service.mapFromDto(clientDto)));
    }

    @DeleteMapping("/clients/{id}")
    public void delete(@PathVariable int id) {
        service.deleteClient(id);
    }
}
