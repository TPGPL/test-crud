package pl.edu.pw.mwotest.cucumber_it;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.ThrowableAssert;
import org.springframework.dao.DataIntegrityViolationException;
import pl.edu.pw.mwotest.models.*;
import pl.edu.pw.mwotest.services.ClientService;
import pl.edu.pw.mwotest.services.OrderService;
import pl.edu.pw.mwotest.services.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@RequiredArgsConstructor
public class OrderServiceIntegrationTestsSteps {
    private int clientId;
    private int productId;
    private int product1Id;
    private OrderLine orderLine;
    private OrderLine orderLine1;
    private List<OrderLine> orderLines;
    private List<OrderLine> orderLinesTooBig;
    private Order order;
    private int requestedId;

    private static ThrowableAssert.ThrowingCallable actionToCall;

    private final ClientService clientService;
    private final ProductService productService;
    private final OrderService orderService;

    @Given("client, product and order line entities are removed")
    public void clientProductAndOrderLineEntitiesAreRemoved() {
        clientService.deleteAll();
        productService.deleteAll();
        orderService.deleteAll();
    }

    @Given("client is created as below:")
    public void clientIsCreatedAsBelow(List<Map<String, String>> data) {
        Client client = new Client(-1, data.get(0).get("name"), data.get(0).get("surname"), data.get(0).get("email"), new ArrayList<>());
        clientService.createClient(client);
        clientId = client.getId();
    }

    @Given("product is created as below:")
    public void productIsCreatedAsBelow(List<Map<String, String>> data) {
        Product product = new Product(-1, data.get(0).get("productName"), Double.parseDouble(data.get(0).get("productPrice")), Integer.parseInt(data.get(0).get("productQuantity")), new ArrayList<>());
        Product product1 = new Product(-1, data.get(1).get("productName"), Double.parseDouble(data.get(1).get("productPrice")), Integer.parseInt(data.get(1).get("productQuantity")), new ArrayList<>());
        productService.createProduct(product);
        productService.createProduct(product1);
        productId = product.getId();
        product1Id = product1.getId();
    }

    @Given("order lines are created as below:")
    public void orderLinesAreCreatedAsBelow(List<Map<String, String>> data) {
        List<OrderLine> orderLines = new ArrayList<>();
        this.orderLine = OrderLine.builder()
                .product(productService.getProduct(this.productId))
                .quantity(Integer.parseInt(data.get(0).get("quantity")))
                .build();
        this.orderLine1 = OrderLine.builder()
                .product(productService.getProduct(this.product1Id))
                .quantity(Integer.parseInt(data.get(1).get("quantity")))
                .build();
        orderLines.add(orderLine);
        orderLines.add(orderLine1);

        this.orderLines = orderLines;
    }

    @Given("order lines too big are created as below:")
    public void orderLinesTooBigAreCreatedAsBelow(List<Map<String, String>> data) {
        List<OrderLine> orderLines = new ArrayList<>();
        OrderLine orderLine = OrderLine.builder()
                .product(productService.getProduct(this.productId))
                .quantity(Integer.parseInt(data.get(0).get("quantity")))
                .build();
        OrderLine orderLine1 = OrderLine.builder()
                .product(productService.getProduct(this.product1Id))
                .quantity(Integer.parseInt(data.get(1).get("quantity")))
                .build();
        orderLines.add(orderLine);
        orderLines.add(orderLine1);

        this.orderLinesTooBig = orderLines;
    }

    @Given("order data as below:")
    public void orderDataAsBelow(List<Map<String, String>> data) {
        this.order = Order.builder()
                .client(clientService.getClient(this.clientId))
                .status(OrderStatus.valueOf(data.get(0).get("status")))
                .line(this.orderLine)
                .line(this.orderLine1)
                .build();
        this.orderLine.setOrder(this.order);
        this.orderLine1.setOrder(this.order);
    }

    @When("I create order")
    public void iCreateOrder() {
        this.order = orderService.createOrder(order);
    }

    @Then("I should see order created successfully")
    public void iShouldSeeOrderCreatedSuccessfully() {
        var retrievedOrder = orderService.getOrder(order.getId());
        assertThat(retrievedOrder).isNotNull();
        assertThat(order.getStatus()).isEqualTo(retrievedOrder.getStatus());
        assertIterableEquals(order.getLines(), retrievedOrder.getLines());
        assertThat(order.getClient()).isEqualTo(retrievedOrder.getClient());
    }

    @When("I try to create an order")
    public void iTryToCreateAnOrder() {
        actionToCall = () -> orderService.createOrder(order);
    }

    @When("I try to create an order with null status")
    public void iTryToCreateAnOrderWithNullStatus() {
        var invalidOrder = new Order(-1, clientService.getClient(this.clientId), null, this.orderLines);
        actionToCall = () -> orderService.createOrder(invalidOrder);
    }

    @Then("Order creation throws Validation Exception")
    public void orderCreationThrowsValidationException() {
        assertThatThrownBy(actionToCall).isInstanceOf(ConstraintViolationException.class);
    }

    @When("I try to create an order with null client")
    public void iTryToCreateAnOrderWithNullClient() {
        var invalidOrder = new Order(-1, null, OrderStatus.New, this.orderLines);
        actionToCall = () -> orderService.createOrder(invalidOrder);
    }

    @When("I create order with empty order lines")
    public void iCreateOrderWithEmptyOrderLines() {
        var invalidOrder = new Order(-1, clientService.getClient(this.clientId), OrderStatus.New, new ArrayList<>());
        actionToCall = () -> orderService.createOrder(invalidOrder);
    }

    @When("I create order with order line quantity more than stock")
    public void iCreateOrderWithOrderLineQuantityMoreThanStock() {
        var invalidOrder = new Order(-1, clientService.getClient(this.clientId), OrderStatus.New, this.orderLinesTooBig);
        actionToCall = () -> orderService.createOrder(invalidOrder);
    }

    @Given("order id is max+{int}")
    public void orderIdIsMax(int arg0) {
        requestedId = StreamSupport.stream(orderService.getAllOrders().spliterator(), false).mapToInt(Order::getId).max().orElse(0) + arg0;
    }

    @When("I read order by id")
    public void iReadOrderById() {
        order = orderService.getOrder(requestedId);
    }

    @Then("order is null")
    public void orderIsNull() {
        assertThat(order).isNull();
    }

    @And("order is created if not exists")
    public void orderIsCreatedIfNotExists() {
        try {
            requestedId = orderService.createOrder(order).getId();
        } catch (DataIntegrityViolationException ignored) {
            StreamSupport.stream(orderService.getAllOrders().spliterator(), false).findFirst().ifPresentOrElse(x -> requestedId = x.getId(), () -> {
                throw new RuntimeException("Order not found");
            });
        }
    }

    @Then("I should see order data as below:")
    public void iShouldSeeOrderDataAsBelow(List<Map<String, String>> data) {
        var retrievedOrder = orderService.getOrder(order.getId());
        assertThat(retrievedOrder).isNotNull();
        assertThat(retrievedOrder.getStatus()).isEqualTo(OrderStatus.valueOf(data.get(0).get("status")));
    }

    @When("I update order by id")
    public void iUpdateOrderById() {
        this.order = Order.builder()
                .client(clientService.getClient(this.clientId))
                .status(OrderStatus.InProgress)
                .line(this.orderLine)
                .line(this.orderLine1)
                .build();
        actionToCall = () -> orderService.updateOrder(requestedId, this.order);
    }

    @Then("order not found exception is thrown")
    public void orderNotFoundExceptionIsThrown() {
        assertThatThrownBy(actionToCall).isInstanceOf(IllegalArgumentException.class);
    }

    @When("I update order with below data:")
    public void iUpdateOrderWithBelowData(List<Map<String, String>> data) {
        var status = OrderStatus.valueOf(data.get(0).get("status"));
        order.setStatus(status);
        order = orderService.updateOrder(requestedId, order);
    }

    @When("I delete order")
    public void iDeleteOrder() {
        orderService.deleteOrder(requestedId);
    }

    @Then("order is not in database anymore")
    public void orderIsNotInDatabaseAnymore() {
        assertThat(orderService.getOrder(requestedId)).isNull();
    }
}
