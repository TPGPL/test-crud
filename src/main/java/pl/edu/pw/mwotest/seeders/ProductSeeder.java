package pl.edu.pw.mwotest.seeders;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.pw.mwotest.models.Product;
import pl.edu.pw.mwotest.services.ProductService;

import java.util.Random;

import static pl.edu.pw.mwotest.seeders.Properties.DATA_SEED;
import static pl.edu.pw.mwotest.seeders.Properties.NO_OF_DATA;

@Component
public class ProductSeeder {
    private final ProductService service;
    private final Faker faker;

    @Autowired
    public ProductSeeder(ProductService service) {
        this.service = service;
        this.faker = new Faker(new Random(DATA_SEED));
    }

    public void seed() {
        var currData = service.getAllProducts();

        if (currData.iterator().hasNext()) {
            return;
        }

        for (int i = 0; i < NO_OF_DATA; i++) {
            var product = Product.builder()
                    .name(faker.commerce().productName())
                    .price(Math.round(faker.random().nextDouble() * 10000) / 100.0)
                    .stockQuantity(faker.random().nextInt(1, 100))
                    .build();

            try {
                service.createProduct(product);
            } catch (Exception ignored) {
            }
        }
    }
}
