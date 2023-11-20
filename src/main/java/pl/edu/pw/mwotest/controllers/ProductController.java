package pl.edu.pw.mwotest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.mwotest.models.Product;
import pl.edu.pw.mwotest.services.ProductService;

import java.util.ArrayList;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService service;

    @Autowired
    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public String get(Model model) {
        var products = new ArrayList<Product>();
        service.getAllProducts().forEach(products::add);

        model.addAttribute("products", products);

        return "products/index";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        service.deleteProduct(id);

        return "redirect:/products";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("product", new Product());

        return "products/create";
    }

    @PostMapping("/create")
    public String save(@ModelAttribute Product dto) {
        try {
            service.createProduct(dto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable int id, Model model) {
        var product = service.getProduct(id);

        model.addAttribute("updateId", id);
        model.addAttribute("product", product);

        return "products/update";
    }

    @PostMapping("/update/{id}")
    public String saveWithUpdate(@PathVariable int id, @ModelAttribute Product dto) {
        try {
            service.updateProduct(id, dto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/products";
    }
}
