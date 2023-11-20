package pl.edu.pw.mwotest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.edu.pw.mwotest.seeders.ClientSeeder;
import pl.edu.pw.mwotest.seeders.OrderSeeder;
import pl.edu.pw.mwotest.seeders.ProductSeeder;

@Component
public class DataLoader implements CommandLineRunner {
    private final ClientSeeder clientSeeder;
    private final ProductSeeder productSeeder;
    private final OrderSeeder orderSeeder;

    @Autowired
    public DataLoader(ClientSeeder clientSeeder, ProductSeeder productSeeder, OrderSeeder orderSeeder) {
        this.clientSeeder = clientSeeder;
        this.productSeeder = productSeeder;
        this.orderSeeder = orderSeeder;
    }

    @Override
    public void run(String... args) {
        clientSeeder.seed();
        productSeeder.seed();
        orderSeeder.seed();
    }
}
