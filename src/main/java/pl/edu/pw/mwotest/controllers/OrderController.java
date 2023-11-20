package pl.edu.pw.mwotest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.mwotest.dtos.OrderDto;
import pl.edu.pw.mwotest.models.Order;
import pl.edu.pw.mwotest.services.OrderService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class OrderController {
    private final OrderService service;

    @Autowired
    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping("/orders/{id}")
    public Order get(@PathVariable int id) {
        return service.getOrder(id);
    }


    @GetMapping("/orders")
    public List<Order> getAll() {
        List<Order> orders = new ArrayList<>();

        service.getAllOrders().forEach(orders::add);

        return orders;
    }

    @PostMapping("/orders")
    public Order create(@RequestBody OrderDto dto) {
        return service.createOrder(service.mapFromDto(dto));
    }

    @PatchMapping("/orders/{id}")
    public Order update(@PathVariable int id, @RequestBody OrderDto dto) {
        return service.updateOrder(id, service.mapFromDto(dto));
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
