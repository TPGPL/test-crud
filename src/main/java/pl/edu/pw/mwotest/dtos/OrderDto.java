package pl.edu.pw.mwotest.dtos;

import lombok.*;
import pl.edu.pw.mwotest.models.Order;
import pl.edu.pw.mwotest.models.OrderLine;
import pl.edu.pw.mwotest.models.OrderStatus;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private int id;
    private int clientId;
    private OrderStatus status;
    private int lineCount;
    private double totalValue;

    public static OrderDto mapToDto(Order order) {
        if (order == null) return null;

        OrderDto dto = new OrderDto();
        dto.id = order.getId();
        dto.clientId = order.getClient().getId();
        dto.status = order.getStatus();
        dto.lineCount = 0;
        dto.totalValue = 0;

        for (OrderLine line : order.getLines()) {
            dto.lineCount++;
            dto.totalValue += line.getProduct().getPrice() * line.getQuantity();
        }

        dto.totalValue = Math.round(dto.totalValue * 100) / 100.0;

        return dto;
    }
}

