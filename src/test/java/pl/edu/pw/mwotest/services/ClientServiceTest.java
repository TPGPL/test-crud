package pl.edu.pw.mwotest.services;

import net.bytebuddy.dynamic.DynamicType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.edu.pw.mwotest.dtos.ClientDto;
import pl.edu.pw.mwotest.models.Client;
import pl.edu.pw.mwotest.repositories.ClientRepository;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ClientServiceTest {
    @Autowired
    private ClientService service;

    @MockBean
    private ClientRepository repository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void cleanup() {
        Mockito.reset(repository);
    }

    @ParameterizedTest
    @MethodSource("getData")
    public void getClient_forValidInput_returnsClient(int id, String name) {
        // arrange
        Client client = new Client();
        client.setId(id);
        client.setName(name);
        Optional<Client> op = Optional.of(client);

        Mockito.when(repository.findById(id)).thenReturn(op);

        // act
        Client result = service.getClient(id);

        assertNotNull(result);
        assertThat(result.getName()).isEqualTo(name);

    }

    @Test
    public void deleteClient_forValidInput_executedOnce() {
        // arrange
        int id = 1;
        // act
        service.deleteClient(id);

        // assert
        Mockito.verify(repository, times(1)).deleteById(id);
    }

    static Stream<Arguments> getData() {
        return Stream.of(
                Arguments.of(1,"Test"),
                Arguments.of(2,"Jan"),
                Arguments.of(3,"John")
        );
    }
}