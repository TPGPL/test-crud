package pl.edu.pw.mwotest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

import java.util.List;

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
        public void readNonExistentProductById() {
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
    }
}
