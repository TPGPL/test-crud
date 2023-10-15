package pl.edu.pw.mwotest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.mwotest.dtos.ProductDto;
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

    @PostMapping("/products/create")
    public ProductDto create(@RequestBody ProductDto dto) {
        return ProductDto.mapToDto(service.createProduct(dto));
    }

    @GetMapping("/products")
    public List<ProductDto> getAll() {
        List<ProductDto> products = new ArrayList<>();
        service.getAllProducts().forEach((x) -> products.add(ProductDto.mapToDto(x)));

        return products;
    }

    @GetMapping("/products/{id}")
    public ProductDto get(@PathVariable int id) {
        return ProductDto.mapToDto(service.getProduct(id));
    }

    @PatchMapping("/products/{id}")
    public ProductDto update(@PathVariable int id, @RequestBody ProductDto dto) {
        return ProductDto.mapToDto(service.updateProduct(id, dto));
    }

    @DeleteMapping("/products/{id}")
    public void delete(@PathVariable int id) {
        service.deleteProduct(id);
    }
}
