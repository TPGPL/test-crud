package pl.edu.pw.mwotest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.edu.pw.mwotest.dtos.OrderDto;
import pl.edu.pw.mwotest.services.OrderService;

import java.util.ArrayList;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService service;

    @Autowired
    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public String get(Model model) {
        var orders = service.getAllOrders();
        var orderDtos = new ArrayList<OrderDto>();
        orders.forEach(x -> orderDtos.add(OrderDto.mapToDto(x)));

        model.addAttribute("orders", orderDtos);

        return "orders/index";
    }
}
