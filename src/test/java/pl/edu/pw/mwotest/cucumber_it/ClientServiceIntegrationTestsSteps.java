package pl.edu.pw.mwotest.cucumber_it;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.ThrowableAssert;
import org.springframework.dao.DataIntegrityViolationException;
import pl.edu.pw.mwotest.models.Client;
import pl.edu.pw.mwotest.services.ClientService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@RequiredArgsConstructor
public class ClientServiceIntegrationTestsSteps extends SpringIntegrationCucumberTests {
    private static Client client;
    private static ThrowableAssert.ThrowingCallable actionToCall;
    private static int requestedId;
    private final ClientService clientService;

    private static <T> T proxyNull(Object obj, Class<T> clazz) {
        return obj == null ? null : clazz.cast(obj);
    }

    private static String proxyBlank(Object obj) {
        if (obj == null) return null;
        var s = obj.toString();
        return s.isBlank() ? null : s;
    }

    @Given("client data as below:")
    public void clientDataAsBelow(List<Map<String, String>> data) {
        client = new Client(-1, proxyNull(data.get(0).get("name"), String.class), proxyNull(data.get(0).get("surname"), String.class), proxyBlank(data.get(0).get("email")), new ArrayList<>());
    }

    @When("I create client")
    public void iCreateClient() {
        client = clientService.createClient(client);
    }

    @Then("I should see client created successfully")
    public void iShouldSeeClientCreatedSuccessfully() {
        var retrievedClient = clientService.getClient(client.getId());
        assertThat(retrievedClient).isNotNull();
        assertThat(client.getName()).isEqualTo(retrievedClient.getName());
        assertThat(client.getSurname()).isEqualTo(retrievedClient.getSurname());
        assertThat(client.getEmail()).isEqualTo(retrievedClient.getEmail());
    }

    @When("I try to create a client")
    public void iTryToCreateAClient() {
        actionToCall = () -> clientService.createClient(client);
    }

    @Then("Client creation throws Validation Exception")
    public void clientCreationThrowsValidationException() {
        assertThatThrownBy(actionToCall).isInstanceOf(ConstraintViolationException.class);
    }

    @And("client is created")
    public void clientIsCreated() {
        client = clientService.createClient(client);
    }

    @When("I create client with duplicate email")
    public void iCreateClientWithDuplicateEmail() {
        var client2 = new Client(-1, client.getName(), client.getSurname(), client.getEmail(), new ArrayList<>());
        actionToCall = () -> clientService.createClient(client2);
    }

    @Then("client creation throws Unique Constraint Exception")
    public void clientCreationThrowsUniqueConstraintException() {
        assertThatThrownBy(actionToCall).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Given("client id is max+{int}")
    public void clientIdIsMax(int arg0) {
        requestedId = StreamSupport.stream(clientService.getAllClients().spliterator(), false).mapToInt(Client::getId).max().orElse(0) + arg0;
    }


    @When("I read client by id")
    public void iReadClientById() {
        client = clientService.getClient(requestedId);
    }

    @Then("client is null")
    public void clientIsNull() {
        assertThat(client).isNull();
    }

    @When("I read client")
    public void iReadClient() {
        client = clientService.getClient(requestedId);
    }


    @Then("I should see client data as below:")
    public void iShouldSeeClientDataAsBelow(List<Map<String, String>> data) {
        assertThat(client).isNotNull();
        assertThat(client.getName()).isEqualTo(proxyNull(data.get(0).get("name"), String.class));
        assertThat(client.getSurname()).isEqualTo(proxyNull(data.get(0).get("surname"), String.class));
        assertThat(client.getEmail()).isEqualTo(proxyNull(data.get(0).get("email"), String.class));
    }

    @When("I update client by id")
    public void iUpdateClientById() {
        actionToCall = () -> clientService.updateClient(requestedId, new Client(-1, "Jan", "Kowalski", "jan@wp.pl", new ArrayList<>()));
    }

    @Then("client not found exception is thrown")
    public void clientNotFoundExceptionIsThrown() {
        assertThatThrownBy(actionToCall).isInstanceOf(IllegalArgumentException.class);
    }


    @When("I update client with below data:")
    public void iUpdateClientWithBelowData(List<Map<String, String>> data) {
        client = clientService.updateClient(requestedId, new Client(-1, proxyBlank(data.get(0).get("name")), proxyBlank(data.get(0).get("surname")), proxyBlank(data.get(0).get("email")), new ArrayList<>()));
    }

    @And("client is created if not exists")
    public void clientIsCreatedIfNotExists() {
        try {
            requestedId = clientService.createClient(client).getId();
        } catch (DataIntegrityViolationException ignored) {
            StreamSupport.stream(clientService.getAllClients().spliterator(), false).filter(x -> x.getEmail().equals(client.getEmail())).findFirst().ifPresentOrElse(x -> requestedId = x.getId(), () -> {
                throw new RuntimeException("Client not found");
            });
        }
    }

    @When("I delete client")
    public void iDeleteClient() {
        clientService.deleteClient(requestedId);
    }

    @Then("client is not in database anymore")
    public void clientIsNotInDatabaseAnymore() {
        assertThat(clientService.getClient(requestedId)).isNull();
    }
}
