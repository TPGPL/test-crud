package pl.edu.pw.mwotest.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.edu.pw.mwotest.models.Order;
import pl.edu.pw.mwotest.models.OrderStatus;
import pl.edu.pw.mwotest.repositories.OrderRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class OrderServiceTest {
    @Autowired
    private OrderService service;

    @MockBean
    private OrderRepository repository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void cleanup() {
        Mockito.reset(repository);
    }

    @Test
    public void completeOrder_ForInProgress_IsSuccessful() {
        // arrange
        Order order = new Order();
        order.setId(2);
        order.setStatus(OrderStatus.InProgress);

        Mockito.when(repository.findById(2)).thenReturn(Optional.of(order));

        // act
        service.completeOrder(2);

        // assert
        assertThat(order.getStatus()).isEqualTo(OrderStatus.Completed);
    }

    @Test
    public void cancelOrder_ForInProgress_IsSuccessful() {

    }
}
