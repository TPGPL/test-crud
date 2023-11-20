package pl.edu.pw.mwotest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.mwotest.dtos.ProductDto;
import pl.edu.pw.mwotest.models.Product;
import pl.edu.pw.mwotest.services.ProductService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductController {
    private final ProductService service;

    @Autowired
    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping("/products")
    public Product create(@RequestBody ProductDto dto) {
        return service.createProduct(service.mapFromDto(dto));
    }

    @GetMapping("/products")
    public List<Product> getAll() {
        List<Product> products = new ArrayList<>();

        service.getAllProducts().forEach(products::add);

        return products;
    }

    @GetMapping("/products/{id}")
    public Product get(@PathVariable int id) {
        return service.getProduct(id);
    }

    @PatchMapping("/products/{id}")
    public Product update(@PathVariable int id, @RequestBody ProductDto dto) {
        return service.updateProduct(id, service.mapFromDto(dto));
    }

    @DeleteMapping("/products/{id}")
    public void delete(@PathVariable int id) {
        service.deleteProduct(id);
    }
}
