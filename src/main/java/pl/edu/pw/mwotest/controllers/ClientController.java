package pl.edu.pw.mwotest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.mwotest.models.Client;
import pl.edu.pw.mwotest.services.ClientService;

import java.util.ArrayList;

@Controller
@RequestMapping("/clients")
public class ClientController {
    private final ClientService service;

    @Autowired
    public ClientController(ClientService service) {
        this.service = service;
    }

    @GetMapping
    public String get(Model model) {
        var clients = new ArrayList<Client>();
        service.getAllClients().forEach(clients::add);

        model.addAttribute("clients", clients);

        return "clients/index";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        service.deleteClient(id);

        return "redirect:/clients";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("client", new Client());

        return "clients/create";
    }

    @PostMapping("/create")
    public String save(@ModelAttribute Client dto) {
        try {
            service.createClient(dto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/clients";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable int id, Model model) {
        var client = service.getClient(id);

        model.addAttribute("updateId", id);
        model.addAttribute("client", client);

        return "clients/update";
    }

    @PostMapping("/update/{id}")
    public String saveWithUpdate(@PathVariable int id, @ModelAttribute Client dto) {
        try {
            service.updateClient(id, dto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/clients";
    }
}
