package pl.edu.pw.mwotest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.mwotest.dtos.ClientDto;
import pl.edu.pw.mwotest.dtos.ServiceResponse;
import pl.edu.pw.mwotest.services.ClientService;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class ClientController {
    private final ClientService service;

    @Autowired
    public ClientController(ClientService service) {
        this.service = service;
    }

    @PostMapping("/clients")
    public ServiceResponse<ClientDto> create(@RequestBody ClientDto clientDto) {
        try {
            var response = service.createClient(service.mapFromDto(clientDto));

            return ServiceResponse.<ClientDto>builder()
                    .success(true)
                    .data(ClientDto.mapToDto(response))
                    .build();
        } catch (Exception e) {
            return ServiceResponse.<ClientDto>builder()
                    .message(e.getMessage())
                    .success(false)
                    .build();
        }
    }

    @GetMapping("/clients")
    public ServiceResponse<List<ClientDto>> getAll() {
        try {
            List<ClientDto> clients = new ArrayList<>();
            service.getAllClients().forEach((x) -> clients.add(ClientDto.mapToDto(x)));

            return ServiceResponse.<List<ClientDto>>builder()
                    .data(clients)
                    .success(true)
                    .build();
        } catch (Exception e) {
            return ServiceResponse.<List<ClientDto>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @GetMapping("/clients/{id}")
    public ServiceResponse<ClientDto> get(@PathVariable int id) {
        try {
            return ServiceResponse.<ClientDto>builder()
                    .data(ClientDto.mapToDto(service.getClient(id)))
                    .success(true)
                    .build();
        } catch (Exception e) {
            return ServiceResponse.<ClientDto>builder()
                    .message(e.getMessage())
                    .success(false)
                    .build();
        }
    }

    @PatchMapping("/clients/{id}")
    public ServiceResponse<ClientDto> update(@PathVariable int id, @RequestBody ClientDto clientDto) {
        try {
            return ServiceResponse.<ClientDto>builder()
                    .success(true)
                    .data(ClientDto.mapToDto(service.updateClient(id, service.mapFromDto(clientDto))))
                    .build();
        } catch (Exception e) {
            return ServiceResponse.<ClientDto>builder()
                    .message(e.getMessage())
                    .success(false)
                    .build();
        }
    }

    @DeleteMapping("/clients/{id}")
    public void delete(@PathVariable int id) {
        service.deleteClient(id);
    }
}
