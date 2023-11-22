package pl.edu.pw.mwotest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.mwotest.dtos.OrderDto;
import pl.edu.pw.mwotest.dtos.ServiceResponse;
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
    public ServiceResponse<OrderDto> get(@PathVariable int id) {
        try {
            return ServiceResponse.<OrderDto>builder()
                    .success(true)
                    .data(OrderDto.mapToDto(service.getOrder(id)))
                    .build();
        } catch (Exception e) {
            return ServiceResponse.<OrderDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }


    @GetMapping("/orders")
    public ServiceResponse<List<OrderDto>> getAll() {
        try {
            List<OrderDto> orders = new ArrayList<>();

            service.getAllOrders().forEach((x) -> orders.add(OrderDto.mapToDto(x)));

            return ServiceResponse.<List<OrderDto>>builder()
                    .data(orders)
                    .success(true)
                    .build();
        } catch (Exception e) {
            return ServiceResponse.<List<OrderDto>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @PostMapping("/orders")
    public ServiceResponse<OrderDto> create(@RequestBody OrderDto dto) {
        try {
            return ServiceResponse.<OrderDto>builder()
                    .success(true)
                    .data(OrderDto.mapToDto(service.createOrder(service.mapFromDto(dto))))
                    .build();
        } catch (Exception e) {
            return ServiceResponse.<OrderDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @PatchMapping("/orders/{id}")
    public ServiceResponse<OrderDto> update(@PathVariable int id, @RequestBody OrderDto dto) {
        try {
            return ServiceResponse.<OrderDto>builder()
                    .data(OrderDto.mapToDto(service.updateOrder(id, service.mapFromDto(dto))))
                    .success(true)
                    .build();
        } catch (Exception e) {
            return ServiceResponse.<OrderDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @DeleteMapping("/orders/{id}")
    public void delete(@PathVariable int id) {
        service.deleteOrder(id);
    }

    @PutMapping("/orders/{id}/cancel")
    public ServiceResponse<OrderDto> cancel(@PathVariable int id) {
        try {
            service.cancelOrder(id);

            return ServiceResponse.<OrderDto>builder()
                    .success(true)
                    .build();
        } catch (Exception e) {
            return ServiceResponse.<OrderDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @PutMapping("/orders/{id}/submit")
    public ServiceResponse<OrderDto> submit(@PathVariable int id) {
        try {
            service.submitOrder(id);

            return ServiceResponse.<OrderDto>builder()
                    .success(true)
                    .build();
        } catch (Exception e) {
            return ServiceResponse.<OrderDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @PutMapping("/orders/{id}/complete")
    public ServiceResponse<OrderDto> complete(@PathVariable int id) {
        try {
            service.completeOrder(id);

            return ServiceResponse.<OrderDto>builder()
                    .success(true)
                    .build();
        } catch (Exception e) {
            return ServiceResponse.<OrderDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

}
