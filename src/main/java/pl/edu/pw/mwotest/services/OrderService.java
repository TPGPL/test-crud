package pl.edu.pw.mwotest.services;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pw.mwotest.dtos.OrderDto;
import pl.edu.pw.mwotest.dtos.OrderLineDto;
import pl.edu.pw.mwotest.models.Order;
import pl.edu.pw.mwotest.models.OrderLine;
import pl.edu.pw.mwotest.models.OrderStatus;
import pl.edu.pw.mwotest.repositories.OrderRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {
    private final OrderRepository repository;
    private final ProductService productService;
    private final ClientService clientService;
    private final Validator validator;

    @Autowired
    public OrderService(OrderRepository repository,
                        ProductService productService,
                        ClientService clientService,
                        Validator validator) {
        this.repository = repository;
        this.productService = productService;
        this.clientService = clientService;
        this.validator = validator;
    }

    public Order getOrder(int id) {
        return repository.findById(id).orElse(null);
    }

    public Iterable<Order> getAllOrders() {
        return repository.findAll();
    }

    public Order createOrder(OrderDto dto) {
        Order newOrder = new Order(
                -1,
                clientService.getClient(dto.getClientId()),
                OrderStatus.New,
                new ArrayList<>()
        );

        Set<ConstraintViolation<Order>> violations = validator.validate(newOrder);
        Set<ConstraintViolation<OrderLine>> lineViolations;

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        List<OrderLineDto> dtoLines = dto.getLines();
        Set<Integer> productIds = new HashSet<>();

        if (dtoLines != null) {
            for (OrderLineDto lineDto : dtoLines) {
                productIds.add(lineDto.getProductId());
                OrderLine line = new OrderLine(
                        -1,
                        productService.getProduct(lineDto.getProductId()),
                        newOrder,
                        lineDto.getQuantity()

                );

                lineViolations = validator.validate(line);

                if (!lineViolations.isEmpty()) {
                    throw new ConstraintViolationException(lineViolations);
                }

                newOrder.getLines().add(line);
            }

            if (productIds.size() != newOrder.getLines().size()) {
                throw new IllegalArgumentException("Duplicate product lines in order.");
            }
        }

        return repository.save(newOrder);
    }

    public Order updateOrder(int id, OrderDto dto) {
        Order orderToUpdate = repository.findById(id).orElse(null);

        if (orderToUpdate == null) {
            throw new IllegalArgumentException(String.format("The client with ID %d was not found - failed to update.", id));
        } else if (orderToUpdate.getStatus() != OrderStatus.New) {
            throw new IllegalArgumentException("Given order to update is in wrong state: " + orderToUpdate.getStatus());
        }

        orderToUpdate.setClient(clientService.getClient(dto.getClientId()));
        orderToUpdate.getLines().clear();

        Set<ConstraintViolation<Order>> violations = validator.validate(orderToUpdate);
        Set<ConstraintViolation<OrderLine>> lineViolations;

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        List<OrderLineDto> dtoLines = dto.getLines();
        Set<Integer> productIds = new HashSet<>();

        if (dtoLines != null) {
            for (OrderLineDto lineDto : dtoLines) {
                productIds.add(lineDto.getProductId());
                OrderLine line = new OrderLine(
                        -1,
                        productService.getProduct(lineDto.getProductId()),
                        orderToUpdate,
                        lineDto.getQuantity()

                );

                lineViolations = validator.validate(line);

                if (!lineViolations.isEmpty()) {
                    throw new ConstraintViolationException(lineViolations);
                }

                orderToUpdate.getLines().add(line);
            }

            if (productIds.size() != orderToUpdate.getLines().size()) {
                throw new IllegalArgumentException("Duplicate product lines in order.");
            }
        }

        return repository.save(orderToUpdate);
    }

    public void submitOrder(int id) {
        Order order = repository.findById(id).orElse(null);

        if (order == null) {
            throw new IllegalArgumentException("Given order to submit does not exist.");
        } else if (order.getStatus() != OrderStatus.New) {
            throw new IllegalArgumentException("Given order to submit is in wrong state: " + order.getStatus());
        }

        order.setStatus(OrderStatus.InProgress);

        for (OrderLine line : order.getLines()) {
            if (line.getProduct() == null) {
                throw new IllegalArgumentException("Product in order line cannot be null.");
            }

            if (line.getProduct().getStockQuantity() < line.getQuantity()) {
                throw new IllegalArgumentException(String.format("Product id %d has not enough stock to complete order.", line.getProduct().getId()));
            }
            line.getProduct().setStockQuantity(line.getProduct().getStockQuantity() - line.getQuantity());
        }

        repository.save(order);
    }

    public void cancelOrder(int id) {
        Order order = repository.findById(id).orElse(null);

        if (order == null) {
            throw new IllegalArgumentException("Given order to cancel does not exist.");
        } else if (order.getStatus() != OrderStatus.InProgress) {
            throw new IllegalArgumentException("Given order to cancel is in wrong state: " + order.getStatus());
        }

        order.setStatus(OrderStatus.Cancelled);

        for (OrderLine line : order.getLines()) {
            if (line.getProduct() == null) {
                throw new IllegalArgumentException("Product in order line cannot be null.");
            }
            line.getProduct().setStockQuantity(line.getProduct().getStockQuantity() + line.getQuantity());
        }

        repository.save(order);
    }

    public void completeOrder(int id) {
        Order order = repository.findById(id).orElse(null);

        if (order == null) {
            throw new IllegalArgumentException("Given order to complete does not exist.");
        } else if (order.getStatus() != OrderStatus.InProgress) {
            throw new IllegalArgumentException("Given order to complete is in wrong state: " + order.getStatus());
        }

        order.setStatus(OrderStatus.Completed);

        repository.save(order);
    }

    public void deleteOrder(int id) {
        Order order = repository.findById(id).orElse(null);

        if (order != null && order.getStatus() == OrderStatus.InProgress) {
            throw new IllegalArgumentException("Cannot delete in-progress order.");
        }

        repository.deleteById(id);
    }
}
