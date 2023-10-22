package pl.edu.pw.mwotest.services;

import jakarta.validation.Valid;
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

    @Autowired
    public OrderService(OrderRepository repository,
                        ProductService productService,
                        ClientService clientService) {
        this.repository = repository;
        this.productService = productService;
        this.clientService = clientService;
    }

    public Order getOrder(int id) {
        return repository.findById(id).orElse(null);
    }

    public Iterable<Order> getAllOrders() {
        return repository.findAll();
    }

    public Order createOrder(@Valid Order order) {
        if (order.getLines().isEmpty()) throw new IllegalArgumentException("The order lines must not be empty.");

        checkForDuplicateOrderLines(order);

        return repository.save(order);
    }

    public Order updateOrder(int id, @Valid Order order) {
        if (order.getLines().isEmpty()) throw new IllegalArgumentException("The order lines must not be empty.");

        Order orderToUpdate = repository.findById(id).orElse(null);

        if (orderToUpdate == null) {
            throw new IllegalArgumentException(String.format("The client with ID %d was not found - failed to update.", id));
        } else if (orderToUpdate.getStatus() != OrderStatus.New) {
            throw new IllegalArgumentException("Given order to update is in wrong state: " + orderToUpdate.getStatus());
        }

        order.setId(orderToUpdate.getId());

        checkForDuplicateOrderLines(order);

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

    public Order mapFromDto(OrderDto dto) {
        if (dto == null) return null;

        Order order = new Order(
                -1,
                clientService.getClient(dto.getClientId()),
                OrderStatus.New,
                new ArrayList<>());

        List<OrderLineDto> dtoLines = dto.getLines();

        if (dtoLines != null) {
            for (var dtoLine : dtoLines) {
                if (dtoLine == null) continue;

                order.getLines().add(new OrderLine(
                        -1,
                        productService.getProduct(dtoLine.getProductId()),
                        order,
                        dtoLine.getQuantity()
                ));
            }
        }

        return order;
    }

    private void checkForDuplicateOrderLines(Order order) {
        Set<Integer> productIds = new HashSet<>();

        for (var line : order.getLines()) {
            if (line.getProduct() == null) return;

            productIds.add(line.getProduct().getId());
        }

        if (productIds.size() != order.getLines().size()) {
            throw new IllegalArgumentException("Duplicate product lines in order.");
        }
    }
}
