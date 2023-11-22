package pl.edu.pw.mwotest.dtos;

import lombok.*;
import pl.edu.pw.mwotest.models.OrderLine;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class OrderLineDto {
    private int id;
    private int productId;
    private int quantity;
    private double price;

    public static OrderLineDto mapToDto(OrderLine line) {
        OrderLineDto dto = new OrderLineDto();
        dto.id = line.getId();
        dto.productId = line.getProduct().getId();
        dto.quantity = line.getQuantity();
        dto.price = line.getProduct().getPrice();

        return dto;
    }
}
