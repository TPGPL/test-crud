package pl.edu.pw.mwotest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.mwotest.dtos.ProductDto;
import pl.edu.pw.mwotest.dtos.ServiceResponse;
import pl.edu.pw.mwotest.services.ProductService;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class ProductController {
    private final ProductService service;

    @Autowired
    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping("/products")
    public ServiceResponse<ProductDto> create(@RequestBody ProductDto dto) {
        try {
            return ServiceResponse.<ProductDto>builder()
                    .data(ProductDto.mapToDto(service.createProduct(service.mapFromDto(dto))))
                    .success(true)
                    .build();
        } catch (Exception e) {
            return ServiceResponse.<ProductDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @GetMapping("/products")
    public ServiceResponse<List<ProductDto>> getAll() {
        try {
            List<ProductDto> products = new ArrayList<>();

            service.getAllProducts().forEach((x) -> products.add(ProductDto.mapToDto(x)));

            return ServiceResponse.<List<ProductDto>>builder()
                    .data(products)
                    .success(true)
                    .build();
        } catch (Exception e) {
            return ServiceResponse.<List<ProductDto>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @GetMapping("/products/{id}")
    public ServiceResponse<ProductDto> get(@PathVariable int id) {
        try {
            return ServiceResponse.<ProductDto>builder()
                    .data(ProductDto.mapToDto(service.getProduct(id)))
                    .success(true)
                    .build();
        } catch (Exception e) {
            return ServiceResponse.<ProductDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @PatchMapping("/products/{id}")
    public ServiceResponse<ProductDto> update(@PathVariable int id, @RequestBody ProductDto dto) {
        try {
            return ServiceResponse.<ProductDto>builder()
                    .data(ProductDto.mapToDto(service.updateProduct(id, service.mapFromDto(dto))))
                    .success(true)
                    .build();
        } catch (Exception e) {
            return ServiceResponse.<ProductDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @DeleteMapping("/products/{id}")
    public void delete(@PathVariable int id) {
        service.deleteProduct(id);
    }
}
