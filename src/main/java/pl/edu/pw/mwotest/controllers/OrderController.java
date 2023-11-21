package pl.edu.pw.mwotest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.mwotest.dtos.OrderDto;
import pl.edu.pw.mwotest.services.OrderService;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class OrderController {
    private final OrderService service;

    @Autowired
    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping("/orders/{id}")
    public OrderDto get(@PathVariable int id) {
        return OrderDto.mapToDto(service.getOrder(id));
    }


    @GetMapping("/orders")
    public List<OrderDto> getAll() {
        List<OrderDto> orders = new ArrayList<>();

        service.getAllOrders().forEach((x) -> orders.add(OrderDto.mapToDto(x)));

        return orders;
    }

    @PostMapping("/orders")
    public OrderDto create(@RequestBody OrderDto dto) {
        return OrderDto.mapToDto(service.createOrder(service.mapFromDto(dto)));
    }

    @PatchMapping("/orders/{id}")
    public OrderDto update(@PathVariable int id, @RequestBody OrderDto dto) {
        return OrderDto.mapToDto(service.updateOrder(id, service.mapFromDto(dto)));
    }

    @DeleteMapping("/orders/{id}")
    public void delete(@PathVariable int id) {
        service.deleteOrder(id);
    }

    @PutMapping("/orders/{id}/cancel")
    public void cancel(@PathVariable int id) {
        service.cancelOrder(id);
    }

    @PutMapping("/orders/{id}/submit")
    public void submit(@PathVariable int id) {
        service.submitOrder(id);
    }

    @PutMapping("/orders/{id}/complete")
    public void complete(@PathVariable int id) {
        service.completeOrder(id);
    }

}
