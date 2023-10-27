package pl.edu.pw.mwotest;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import pl.edu.pw.mwotest.models.Client;
import pl.edu.pw.mwotest.repositories.ClientRepository;
import pl.edu.pw.mwotest.services.ClientService;

import java.lang.invoke.CallSite;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class ClientServiceIntegrationTests {

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
            assertThatThrownBy(() -> clientService.createClient(client2)).isInstanceOf(DataIntegrityViolationException.class).hasMessageContaining("Unique index or primary key violation");
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
            assertThatThrownBy(() -> clientService.updateClient(id, client)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("not found");
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
        @Test
        public void testCreateClientWithInvalidName() {
            Client client = Client.builder().name("A").surname("ValidSurname").email("valid@example.com").build();

            assertThatThrownBy(() -> clientService.createClient(client)).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Name must be between 2 and 50 characters");
        }

        @Test
        public void testCreateClientWithInvalidSurname() {
            Client client = Client.builder().name("ValidName").surname("S").email("valid@example.com").build();

            assertThatThrownBy(() -> clientService.createClient(client)).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Surname must be between 2 and 50 characters");
        }

        @Test
        public void testCreateClientWithInvalidEmail() {
            Client client = Client.builder().name("ValidName").surname("ValidSurname").email("invalid-email").build();

            assertThatThrownBy(() -> clientService.createClient(client)).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Email must be in a valid format");
        }

        @Test
        public void testCreateClientWithInvalidNameLength() {
            Client client = Client.builder().name("A").surname("ValidSurname").email("valid@example.com").build();

            assertThatThrownBy(() -> clientService.createClient(client)).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Name must be between 2 and 50 characters");
        }

        @Test
        public void testCreateClientWithInvalidSurnameLength() {
            Client client = Client.builder().name("ValidName").surname("S").email("valid@example.com").build();

            assertThatThrownBy(() -> clientService.createClient(client)).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Surname must be between 2 and 50 characters");
        }

        @Test
        public void testCreateClientWithInvalidEmailFormat() {
            Client client = Client.builder().name("ValidName").surname("ValidSurname").email("invalid-email").build();

            assertThatThrownBy(() -> clientService.createClient(client)).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Email must be in a valid format");
        }

        @Test
        public void testCreateClientWithNullName() {
            Client client = Client.builder().name(null).surname("ValidSurname").email("valid@example.com").build();

            assertThatThrownBy(() -> clientService.createClient(client)).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Name must not be null");
        }

        @Test
        public void testCreateClientWithNullSurname() {
            Client client = Client.builder().name("ValidName").surname(null).email("valid@example.com").build();

            assertThatThrownBy(() -> clientService.createClient(client)).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Surname must not be null");
        }

        @Test
        public void testCreateClientWithNullEmail() {
            Client client = Client.builder().name("ValidName").surname("ValidSurname").email(null).build();

            assertThatThrownBy(() -> clientService.createClient(client)).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Email must not be null");
        }
    }

    @Nested
    @Order(3)
    class UpdateConstraintsTests extends ClientTestBase {

        @Test
        public void testUpdateClientWithInvalidName() {
            // given
            Client validClient = Client.builder().name("ValidName").surname("ValidSurname").email("valid@example.com").build();
            validClient = clientRepository.save(validClient);

            // when
            validClient.setName("A");

            // then
            Client finalValidClient = validClient;
            assertThatThrownBy(() -> clientService.updateClient(finalValidClient.getId(), finalValidClient)).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Name must be between 2 and 50 characters");
        }

        @Test
        public void testUpdateClientWithInvalidSurname() {
            // given
            Client validClient = Client.builder().name("ValidName").surname("ValidSurname").email("valid@example.com").build();
            validClient = clientRepository.save(validClient);

            // when
            validClient.setSurname("S");

            // then
            Client finalValidClient = validClient;
            assertThatThrownBy(() -> clientService.updateClient(finalValidClient.getId(), finalValidClient)).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Surname must be between 2 and 50 characters");
        }

        @Test
        public void testUpdateClientWithInvalidEmail() {
            // given
            Client validClient = Client.builder().name("ValidName").surname("ValidSurname").email("valid@example.com").build();
            validClient = clientRepository.save(validClient);

            // when
            validClient.setEmail("invalid-email");

            // then
            Client finalValidClient = validClient;
            assertThatThrownBy(() -> clientService.updateClient(finalValidClient.getId(), finalValidClient)).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Email must be in a valid format");
        }

        @Test
        public void testUpdateClientWithNullName() {
            // given
            Client validClient = Client.builder().name("ValidName").surname("ValidSurname").email("valid@example.com").build();
            validClient = clientRepository.save(validClient);

            // when
            validClient.setName(null);

            // then
            Client finalValidClient = validClient;
            assertThatThrownBy(() -> clientService.updateClient(finalValidClient.getId(), finalValidClient)).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Name must not be null");
        }

        @Test
        public void testUpdateClientWithNullSurname() {
            // given
            Client validClient = Client.builder().name("ValidName").surname("ValidSurname").email("valid@example.com").build();
            validClient = clientRepository.save(validClient);

            // when
            validClient.setSurname(null);

            // then
            Client finalValidClient = validClient;
            assertThatThrownBy(() -> clientService.updateClient(finalValidClient.getId(), finalValidClient)).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Surname must not be null");
        }

        @Test
        public void testUpdateClientWithNullEmail() {
            // given
            Client validClient = Client.builder().name("ValidName").surname("ValidSurname").email("valid@example.com").build();
            validClient = clientRepository.save(validClient);

            // when
            validClient.setEmail(null);

            // then
            Client finalValidClient = validClient;
            assertThatThrownBy(() -> clientService.updateClient(finalValidClient.getId(), finalValidClient)).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Email must not be null");
        }
    }
}


