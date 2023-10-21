package pl.edu.pw.mwotest.dtos;

import lombok.*;
import pl.edu.pw.mwotest.models.Client;
import pl.edu.pw.mwotest.models.Order;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class ClientDto {
    private int id;
    private String name;
    private String surname;
    private String email;
    private List<OrderDto> orders = new ArrayList<>();

    public static ClientDto mapToDto(Client client) {
        if (client == null) return null;

        ClientDto dto = new ClientDto();
        dto.id = client.getId();
        dto.name = client.getName();
        dto.surname = client.getSurname();
        dto.email = client.getEmail();

        for (Order o : client.getOrders()) {
            dto.orders.add(OrderDto.mapToDto(o));
        }

        return dto;
    }

    public static Client mapFromDto(ClientDto dto) {
        return dto != null ? new Client(
                -1,
                dto.name,
                dto.surname,
                dto.email,
                new ArrayList<>()
        ) : null;
    }
}
