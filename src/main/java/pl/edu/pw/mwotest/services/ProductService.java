package pl.edu.pw.mwotest.services;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pw.mwotest.models.Product;
import pl.edu.pw.mwotest.repositories.ProductRepository;

@Service
public class ProductService {
    private ProductRepository repository;
    private Validator validator;

    @Autowired
    public ProductService(ProductRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
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

    public Product createProduct(Product newProduct) {
        var violations = validator.validate(newProduct);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return repository.save(newProduct);
    }

    public Product updateProduct(int id, Product updatedProduct) {
        var violations = validator.validate(updatedProduct);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        var productToUpdate = repository.findById(id).orElse(null);

        if (productToUpdate == null) {
            throw new IllegalArgumentException(String.format("The product with ID %d was not found - failed to update.", id));
        }

        productToUpdate.setName(updatedProduct.getName());
        productToUpdate.setPrice(updatedProduct.getPrice());
        productToUpdate.setStockQuantity(updatedProduct.getStockQuantity());

        return repository.save(productToUpdate);
    }
}
