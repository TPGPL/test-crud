package pl.edu.pw.mwotest.dtos;

import lombok.*;
import pl.edu.pw.mwotest.models.Product;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class ProductDto {
    private int id;
    private String name;
    private double price;
    private int stockQuantity;

    public static ProductDto mapToDto(Product product) {
        if (product == null) return null;

        ProductDto dto = new ProductDto();
        dto.id = product.getId();
        dto.name = product.getName();
        dto.price = product.getPrice();
        dto.stockQuantity = product.getStockQuantity();

        return dto;
    }
}
