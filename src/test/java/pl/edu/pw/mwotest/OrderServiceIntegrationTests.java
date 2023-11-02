package pl.edu.pw.mwotest;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import pl.edu.pw.mwotest.models.Client;
import pl.edu.pw.mwotest.models.OrderLine;
import pl.edu.pw.mwotest.models.Product;
import pl.edu.pw.mwotest.models.Order;
import pl.edu.pw.mwotest.models.OrderStatus;
import pl.edu.pw.mwotest.repositories.OrderRepository;
import pl.edu.pw.mwotest.services.ClientService;
import pl.edu.pw.mwotest.services.OrderService;
import pl.edu.pw.mwotest.services.ProductService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class OrderServiceIntegrationTests {
    @Nested
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    @SpringBootTest
    abstract class OrderTestBase {
        @Autowired
        protected OrderRepository orderRepository;
        @Autowired
        protected OrderService orderService;
        @Autowired
        protected ClientService clientService;
        @Autowired
        protected ProductService productService;

        protected Client client;
        protected Product product;

        @BeforeEach
        public void setup() {
            var clientId = clientService.createClient(
                    Client.builder().name("Zbigniew").surname("Json").email("zbigiew@json.org").build()
            ).getId();
            client = clientService.getClient(clientId);

            var productId = productService.createProduct(
                    Product.builder().name("Piwo").price(2.99).stockQuantity(2136).build()
            ).getId();
            product = productService.getProduct(productId);
        }
    }

    @Nested
    @org.junit.jupiter.api.Order(1)
    class CRUD_OperationTests extends OrderTestBase {
        @Test
        public void createOrderSuccessful() {
            // given
            OrderLine orderLine = OrderLine.builder().product(product).quantity(10).build();
            Order order = Order.builder().client(client).status(OrderStatus.New).line(orderLine).build();
            orderLine.setOrder(order);

            // when
            var id = orderService.createOrder(order).getId();

            // then
            Order retrievedOrder = orderRepository.findById(id).orElse(null);
            assertNotNull(retrievedOrder);
            assertEquals(order.getStatus(), retrievedOrder.getStatus());
            assertEquals(order.getClient(), retrievedOrder.getClient());
            assertIterableEquals(order.getLines(), retrievedOrder.getLines());
        }

        @Test
        public void readOrderByIdSuccessful() {
            // given
            OrderLine orderLine = OrderLine.builder().product(product).quantity(10).build();
            Order order = Order.builder().client(client).status(OrderStatus.New).line(orderLine).build();
            orderLine.setOrder(order);
            var id = orderRepository.save(order).getId();

            // when
            Order retrievedOrder = orderService.getOrder(id);

            // then
            assertNotNull(retrievedOrder);
            assertEquals(order.getStatus(), retrievedOrder.getStatus());
            assertEquals(order.getClient(), retrievedOrder.getClient());
            assertIterableEquals(order.getLines(), retrievedOrder.getLines());
        }

        @Test
        public void readNonExistentOrderById() {
            // given
            var id = 1;

            // when
            Order order = orderService.getOrder(id);

            // then
            assertNull(order);
        }

        @Test
        public void readAllOrdersTest() {
            // given
            OrderLine orderLine1 = OrderLine.builder().product(product).quantity(10).build();
            Order order1 = Order.builder().client(client).status(OrderStatus.New).line(orderLine1).build();
            orderLine1.setOrder(order1);

            OrderLine orderLine2 = OrderLine.builder().product(product).quantity(13).build();
            Order order2 = Order.builder().client(client).status(OrderStatus.New).line(orderLine2).build();
            orderLine2.setOrder(order2);

            List<Order> orders = List.of(order1, order2);
            orderRepository.saveAll(orders);

            // when
            List<Order> retrievedOrders = (List<Order>) orderService.getAllOrders();

            // then
            assertIterableEquals(orders, retrievedOrders);
        }

        @Test
        public void updateOrderSuccessful() {
            // given
            OrderLine orderLine = OrderLine.builder().product(product).quantity(10).build();
            Order order = Order.builder().client(client).status(OrderStatus.New).line(orderLine).build();
            orderLine.setOrder(order);
            var id = orderRepository.save(order).getId();

            // when
            orderService.updateOrder(id, order);

            // then
            Order retrievedOrder = orderRepository.findById(id).orElse(null);
            assertNotNull(retrievedOrder);
            assertEquals(order.getStatus(), retrievedOrder.getStatus());
            assertEquals(order.getClient(), retrievedOrder.getClient());
            assertIterableEquals(order.getLines(), retrievedOrder.getLines());
        }

        @Test
        public void updateNonExistentOrder_shouldThrowException() {
            // given
            OrderLine orderLine = OrderLine.builder().product(product).quantity(10).build();
            Order order = Order.builder().client(client).status(OrderStatus.New).line(orderLine).build();
            orderLine.setOrder(order);
            var id = 2317;

            // when / then
            assertThatThrownBy(() -> orderService.updateOrder(id, order)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void deleteOrderSuccessful() {
            // given
            OrderLine orderLine = OrderLine.builder().product(product).quantity(10).build();
            Order order = Order.builder().client(client).status(OrderStatus.New).line(orderLine).build();
            orderLine.setOrder(order);
            var id = orderRepository.save(order).getId();

            // when
            orderService.deleteOrder(id);

            // then
            assertFalse(orderRepository.existsById(id));
        }

        @Test
        public void deleteNonExistentOrder() {
            // given
            var id = 2317;

            // when / then
            assertThatNoException().isThrownBy(() -> orderService.deleteOrder(id));
        }
    }

    @Nested
    @org.junit.jupiter.api.Order(2)
    class CreateConstraintsTests extends OrderTestBase {
        @Test
        public void createOrderWithNullClient() {
            // given
            OrderLine orderLine = OrderLine.builder().product(product).quantity(10).build();
            Order order = Order.builder().client(null).status(OrderStatus.New).line(orderLine).build();
            orderLine.setOrder(order);

            // when / then
            assertThatThrownBy(() -> orderService.createOrder(order)).isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        public void createOrderWithNullOrderLines() {
            // given
            Order order = Order.builder().client(client).status(OrderStatus.New).lines(null).build();

            // when / then
            assertThatThrownBy(() -> orderService.createOrder(order)).isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        public void createOrderWithEmptyOrderLines() {
            // given
            Order order = Order.builder().client(client).status(OrderStatus.New).build();

            // when / then
            assertThatThrownBy(() -> orderService.createOrder(order)).isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        public void createOrderWithNullOrderLine() {
            // given
            Order order = Order.builder().client(client).status(OrderStatus.New).line(null).build();

            // when / then
            assertThatThrownBy(() -> orderService.createOrder(order)).isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        public void createOrderWithDuplicatedOrderLines() {
            // given
            OrderLine orderLine1 = OrderLine.builder().product(product).quantity(10).build();
            OrderLine orderLine2 = OrderLine.builder().product(product).quantity(13).build();

            Order order = Order.builder().client(client).status(OrderStatus.New).line(orderLine1).line(orderLine2).build();
            orderLine1.setOrder(order);
            orderLine2.setOrder(order);

            // when / then
            assertThatThrownBy(() -> orderService.createOrder(order)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @org.junit.jupiter.api.Order(3)
    class UpdateConstraintsTests extends OrderTestBase {
        @Test
        public void updateOrderWithNullClient() {
            // given
            OrderLine orderLine = OrderLine.builder().product(product).quantity(10).build();
            Order validOrder = Order.builder().client(client).status(OrderStatus.New).line(orderLine).build();
            orderLine.setOrder(validOrder);
            validOrder = orderRepository.save(validOrder);

            // when
            validOrder.setClient(null);

            // then
            Order finalUpdatedOrder = validOrder;
            assertThatThrownBy(() -> orderService.updateOrder(finalUpdatedOrder.getId(), finalUpdatedOrder)).isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        public void updateOrderWithNullOrderLines() {
            // given
            OrderLine orderLine = OrderLine.builder().product(product).quantity(10).build();
            Order validOrder = Order.builder().client(client).status(OrderStatus.New).line(orderLine).build();
            orderLine.setOrder(validOrder);
            validOrder = orderRepository.save(validOrder);

            // when
            validOrder.setLines(null);

            // then
            Order finalUpdatedOrder = validOrder;
            assertThatThrownBy(() -> orderService.updateOrder(finalUpdatedOrder.getId(), finalUpdatedOrder)).isInstanceOf(ConstraintViolationException.class);        }

        @Test
        public void updateOrderWithEmptyOrderLines() {
            // given
            OrderLine orderLine = OrderLine.builder().product(product).quantity(10).build();
            Order validOrder = Order.builder().client(client).status(OrderStatus.New).line(orderLine).build();
            orderLine.setOrder(validOrder);
            validOrder = orderRepository.save(validOrder);

            // when
            validOrder.setLines(new ArrayList<>());

            // then
            Order finalUpdatedOrder = validOrder;
            assertThatThrownBy(() -> orderService.updateOrder(finalUpdatedOrder.getId(), finalUpdatedOrder)).isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        public void updateOrderWithNullOrderLine() {
            // given
            OrderLine orderLine = OrderLine.builder().product(product).quantity(10).build();
            Order validOrder = Order.builder().client(client).status(OrderStatus.New).line(orderLine).build();
            orderLine.setOrder(validOrder);
            validOrder = orderRepository.save(validOrder);

            // when
            validOrder = validOrder.toBuilder().line(null).build();

            // then
            Order finalUpdatedOrder = validOrder;
            assertThatThrownBy(() -> orderService.updateOrder(finalUpdatedOrder.getId(), finalUpdatedOrder)).isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        public void updateOrderWithDuplicatedOrderLines() {
            // given
            OrderLine orderLine1 = OrderLine.builder().product(product).quantity(10).build();
            Order validOrder = Order.builder().client(client).status(OrderStatus.New).line(orderLine1).build();
            orderLine1.setOrder(validOrder);
            validOrder = orderRepository.save(validOrder);

            // when
            OrderLine orderLine2 = OrderLine.builder().product(product).order(validOrder).quantity(13).build();
            validOrder = validOrder.toBuilder().line(orderLine2).build();

            // when / then
            Order finalUpdatedOrder = validOrder;
            assertThatThrownBy(() -> orderService.updateOrder(finalUpdatedOrder.getId(), finalUpdatedOrder)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @org.junit.jupiter.api.Order(4)
    class OrderFlowTests {
        // Tests for submit, cancel and complete methods of OrderService
        // Masa's code goes here
    }
}
