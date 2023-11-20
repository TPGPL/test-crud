package pl.edu.pw.mwotest.seeders;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.pw.mwotest.models.Client;
import pl.edu.pw.mwotest.services.ClientService;

import java.util.Random;

import static pl.edu.pw.mwotest.seeders.Properties.DATA_SEED;
import static pl.edu.pw.mwotest.seeders.Properties.NO_OF_DATA;

@Component
public class ClientSeeder {
    private final ClientService service;
    private final Faker faker;

    @Autowired
    public ClientSeeder(ClientService service) {
        this.service = service;
        this.faker = new Faker(new Random(DATA_SEED));
    }

    public void seed() {
        var currData = service.getAllClients();

        if (currData.iterator().hasNext()) {
            return;
        }

        for (int i = 0; i < NO_OF_DATA; i++) {
            var client = Client.builder()
                    .name(faker.name().firstName())
                    .surname(faker.name().lastName())
                    .email(faker.internet().emailAddress())
                    .build();

            try {
                service.createClient(client);
            } catch (Exception ignored) {
            }
        }
    }
}
