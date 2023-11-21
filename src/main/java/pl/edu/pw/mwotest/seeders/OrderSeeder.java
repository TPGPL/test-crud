package pl.edu.pw.mwotest.seeders;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.pw.mwotest.models.Order;
import pl.edu.pw.mwotest.models.OrderLine;
import pl.edu.pw.mwotest.models.OrderStatus;
import pl.edu.pw.mwotest.services.ClientService;
import pl.edu.pw.mwotest.services.OrderService;
import pl.edu.pw.mwotest.services.ProductService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static pl.edu.pw.mwotest.seeders.Properties.DATA_SEED;
import static pl.edu.pw.mwotest.seeders.Properties.NO_OF_DATA;

@Component
public class OrderSeeder {
    private final OrderService service;
    private final ClientService clientService;
    private final ProductService productService;
    private final Faker faker;

    @Autowired
    public OrderSeeder(OrderService service, ClientService clientService, ProductService productService) {
        this.service = service;
        this.clientService = clientService;
        this.productService = productService;
        this.faker = new Faker(new Random(DATA_SEED));
    }

    public void seed() {
        var currData = service.getAllOrders();

        if (currData.iterator().hasNext()) {
            return;
        }

        int maxValue = Math.max(NO_OF_DATA - 5, NO_OF_DATA / 2);

        for (int i = 0; i < NO_OF_DATA; i++) {
            var lines = new ArrayList<OrderLine>();

            var order = Order.builder()
                    .client(clientService.getClient(faker.random().nextInt(maxValue)))
                    .status(OrderStatus.New)
                    .build();

            for (int j = 0; j < 5; j++) {
                lines.add(
                        OrderLine.builder()
                                .product(productService.getProduct(faker.random().nextInt(maxValue)))
                                .order(order)
                                .quantity(faker.random().nextInt(1, 100))
                                .build()
                );
            }

            order.setLines(lines);

            try {
                service.createOrder(order);
            } catch (Exception ignored) {
            }
        }
    }
}
