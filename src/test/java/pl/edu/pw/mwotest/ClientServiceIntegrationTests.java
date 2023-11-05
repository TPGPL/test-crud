package pl.edu.pw.mwotest;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import pl.edu.pw.mwotest.models.Client;
import pl.edu.pw.mwotest.repositories.ClientRepository;
import pl.edu.pw.mwotest.services.ClientService;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class ClientServiceIntegrationTests {

    private static final String STR_45 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private static final String STR_50 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";


    @Nested
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    @SpringBootTest
    abstract class ClientTestBase {
        @Autowired
        protected ClientRepository clientRepository;
        @Autowired
        protected ClientService clientService;
    }

    @Nested
    @Order(1)
    class CRUD_OperationTests extends ClientTestBase {
        @Test
        public void createClientSuccessful() {
            // given
            Client client = Client.builder().name("Zbigniew").surname("Json").email("zbigiew@json.org").build();

            // when
            var id = clientService.createClient(client).getId();

            // then
            Client retrievedClient = clientRepository.findById(id).orElse(null);
            assertNotNull(retrievedClient);
            assertEquals(client.getName(), retrievedClient.getName());
            assertEquals(client.getSurname(), retrievedClient.getSurname());
            assertEquals(client.getEmail(), retrievedClient.getEmail());
        }

        @Test
        public void createDuplicateEmailClient_shouldThrowException() {
            // given
            Client client = Client.builder().name("Zbigniew").surname("Json").email("zbigiew@json.org").build();
            Client client2 = Client.builder().name("Zbigniew").surname("Json2").email("zbigiew@json.org").build();
            clientRepository.save(client);

            // when / then
            assertThatThrownBy(() -> clientService.createClient(client2)).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        public void readClientByIdTestSuccess() {
            // given
            Client client = Client.builder().name("Zbigniew").surname("Json").email("zbigiew@json.org").build();
            var id = clientRepository.save(client).getId();

            // when
            Client retrievedClient = clientService.getClient(id);

            // then
            assertNotNull(retrievedClient);
            assertEquals(client.getName(), retrievedClient.getName());
            assertEquals(client.getSurname(), retrievedClient.getSurname());
            assertEquals(client.getEmail(), retrievedClient.getEmail());
        }

        @Test
        public void readNonExistentClientById() {
            // given
            var id = 1;

            // when
            Client retrievedClient = clientService.getClient(id);

            // then
            assertNull(retrievedClient);
        }

        @Test
        public void getAllTest() {
            // given
            Client client = Client.builder().name("1 name").surname("1 surname").email("1@email.com").build();
            Client client2 = Client.builder().name("2 name").surname("2 surname").email("2@email.com").build();
            List<Client> clients = List.of(client, client2);
            clientRepository.saveAll(clients);

            // when
            List<Client> receivedClients = StreamSupport.stream(clientService.getAllClients().spliterator(), false).toList();

            // then
            assertEquals(receivedClients, clients);
        }

        @Test
        public void updateClientSuccessful() {
            // given
            Client client = Client.builder().name("new name").surname("new surname").email("new@email.com").build();
            var id = clientRepository.save(client).getId();

            // when
            clientService.updateClient(id, client);

            // then
            Client retrievedClient = clientRepository.findById(id).orElse(null);
            assertNotNull(retrievedClient);
            assertEquals(client.getName(), retrievedClient.getName());
            assertEquals(client.getSurname(), retrievedClient.getSurname());
            assertEquals(client.getEmail(), retrievedClient.getEmail());
        }

        @Test
        public void updateNonExistentClient() {
            // given
            Client client = Client.builder().name("new name").surname("new surname").email("new@email.com").build();
            var id = 20000;

            // when / then
            assertThatThrownBy(() -> clientService.updateClient(id, client)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void deleteSuccessful() {
            // given
            Client client = Client.builder().name("new name").surname("new surname").email("new@email.com").build();
            client = clientRepository.save(client);

            // when
            clientService.deleteClient(client.getId());

            // then
            assertTrue(clientRepository.findById(client.getId()).isEmpty());
        }
    }

    @Nested
    @Order(2)
    class CreateConstraintsTests extends ClientTestBase {
        @ParameterizedTest
        @ValueSource(strings = {"a", STR_50 + "a"})
        @NullSource
        public void testCreateClientWithInvalidName(String name) {
            Client client = Client.builder().name(name).surname("ValidSurname").email("valid@example.com").build();

            assertThatThrownBy(() -> clientService.createClient(client)).isInstanceOf(ConstraintViolationException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", STR_50+"a"})
        @NullSource
        public void testCreateClientWithInvalidSurname(String surname) {
            Client client = Client.builder().name("ValidName").surname(surname).email("valid@example.com").build();

            assertThatThrownBy(() -> clientService.createClient(client)).isInstanceOf(ConstraintViolationException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "a@w.pl", STR_45 +"@6c.pl", "invalid-email", "invalid-email@", "invalid-email@.pl"})
        @NullSource
        public void testCreateClientWithInvalidEmail(String mail) {
            Client client = Client.builder().name("ValidName").surname("ValidSurname").email(mail).build();

            assertThatThrownBy(() -> clientService.createClient(client)).isInstanceOf(ConstraintViolationException.class);
        }


        @ParameterizedTest
        @ValueSource(strings = {"aa", STR_50, "Andrzej"})
        public void testCreateClientWithValidName(String name) {
            Client client = Client.builder().name(name).surname("ValidSurname").email("valid@example.com").build();

            assertDoesNotThrow(() -> clientService.createClient(client));
        }

        @ParameterizedTest
        @ValueSource(strings = {"aa", STR_50, "Andrzejewski"})
        public void testCreateClientWithValidSurname(String surname) {
            Client client = Client.builder().name("ValidName").surname(surname).email("valid@example.com").build();

            assertDoesNotThrow(() -> clientService.createClient(client));
        }

        @ParameterizedTest
        @ValueSource(strings = {"a@7c.pl", STR_45+"@p.pl", "andrzej@json.pl", "lubie@localhost"})
        public void testCreateClientWithValidEmail(String mail) {
            Client client = Client.builder().name("ValidName").surname("ValidSurname").email(mail).build();

            assertDoesNotThrow(() -> clientService.createClient(client));
        }
    }

    @Nested
    @Order(3)
    class UpdateConstraintsTests extends ClientTestBase {

        @ParameterizedTest
        @ValueSource(strings = {"a", STR_50+"a"})
        @NullSource
        public void testUpdateClientWithInvalidName(String name) {
            // given
            Client validClient = Client.builder().name("ValidName").surname("ValidSurname").email("valid@example.com").build();
            validClient = clientRepository.save(validClient);

            // when
            validClient.setName(name);

            // then
            Client finalValidClient = validClient;
            assertThatThrownBy(() -> clientService.updateClient(finalValidClient.getId(), finalValidClient)).isInstanceOf(ConstraintViolationException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", STR_50+"a"})
        @NullSource
        public void testUpdateClientWithInvalidSurname(String surname) {
            // given
            Client validClient = Client.builder().name("ValidName").surname("ValidSurname").email("valid@example.com").build();
            validClient = clientRepository.save(validClient);

            // when
            validClient.setSurname(surname);

            // then
            Client finalValidClient = validClient;
            assertThatThrownBy(() -> clientService.updateClient(finalValidClient.getId(), finalValidClient)).isInstanceOf(ConstraintViolationException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "a@w.pl", STR_45 +"@6c.pl", "invalid-email", "invalid-email@", "invalid-email@.pl"})
        @NullSource
        public void testUpdateClientWithInvalidEmail(String mail) {
            // given
            Client validClient = Client.builder().name("ValidName").surname("ValidSurname").email("valid@example.com").build();
            validClient = clientRepository.save(validClient);

            // when
            validClient.setEmail(mail);

            // then
            Client finalValidClient = validClient;
            assertThatThrownBy(() -> clientService.updateClient(finalValidClient.getId(), finalValidClient)).isInstanceOf(ConstraintViolationException.class);
        }
    }
}