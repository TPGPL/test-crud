package pl.edu.pw.mwotest.dtos;

import lombok.*;
import pl.edu.pw.mwotest.models.Order;
import pl.edu.pw.mwotest.models.OrderLine;
import pl.edu.pw.mwotest.models.OrderStatus;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private int id;
    private int clientId;
    private OrderStatus status;
    private List<OrderLineDto> lines = new ArrayList<>();


    public static OrderDto mapToDto(Order order) {
        if (order == null) return null;

        OrderDto dto = new OrderDto();
        dto.id = order.getId();
        dto.clientId = order.getClient().getId();
        dto.status = order.getStatus();

        for (OrderLine line : order.getLines()) {
            dto.lines.add(OrderLineDto.mapToDto(line));
        }

        return dto;
    }
}

