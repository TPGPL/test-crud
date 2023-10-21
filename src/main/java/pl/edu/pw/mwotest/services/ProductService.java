package pl.edu.pw.mwotest.services;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pw.mwotest.dtos.ProductDto;
import pl.edu.pw.mwotest.models.Product;
import pl.edu.pw.mwotest.repositories.ProductRepository;

import java.util.ArrayList;

@Service
public class ProductService {
    private final ProductRepository repository;

    @Autowired
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product getProduct(int id) {
        return repository.findById(id).orElse(null);
    }

    public Iterable<Product> getAllProducts() {
        return repository.findAll();
    }

    public void deleteProduct(int id) {
        repository.deleteById(id);
    }

    public Product createProduct(@Valid Product product) {
        return repository.save(product);
    }

    public Product updateProduct(int id, @Valid Product product) {
        var productToUpdate = repository.findById(id).orElse(null);

        if (productToUpdate == null) {
            throw new IllegalArgumentException(String.format("The product with ID %d was not found - failed to update.", id));
        }

        productToUpdate.setName(product.getName());
        productToUpdate.setPrice(product.getPrice());
        productToUpdate.setStockQuantity(product.getStockQuantity());

        return repository.save(productToUpdate);
    }

    public Product mapFromDto(ProductDto dto) {
        return dto != null ? new Product(
                -1,
                dto.getName(),
                dto.getPrice(),
                dto.getStockQuantity(),
                new ArrayList<>()
        ) : null;
    }
}
