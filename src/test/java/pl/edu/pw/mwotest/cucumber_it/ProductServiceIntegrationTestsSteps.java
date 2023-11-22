package pl.edu.pw.mwotest.cucumber_it;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.mwotest.models.Product;
import pl.edu.pw.mwotest.services.ProductService;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class ProductServiceIntegrationTestsSteps {
    private static Product product;
    private final ProductService productService;

    private static String proxyBlank(Object obj) {
        if (obj == null) return null;
        var s = obj.toString();
        return s.isBlank() ? null : s;
    }

    private static Number proxyZero(Object obj, Class<? extends Number> clazz) {
        if (obj == null)
            return clazz == Double.class ? 0.0d : 0;

        if (clazz.isInstance(obj))
            return (Number) obj;

        try {
            return clazz == Double.class ? Double.parseDouble(obj.toString()) : Integer.parseInt(obj.toString());
        } catch (NumberFormatException e) {
            return clazz == Double.class ? 0.0d : 0;
        }
    }

    @Given("product data as below")
    public void productDataAsBelow(List<Map<String, String>> data) {
        product = Product.builder()
                .name(proxyBlank(data.get(0).get("name")))
                .price(proxyZero(data.get(0).get("price"), Double.class).doubleValue())
                .stockQuantity(proxyZero(data.get(0).get("stockQuantity"), Integer.class).intValue())
                .build();
    }

    @When("I create product")
    public void iCreateProduct() {
        product = productService.createProduct(product);
    }

    @Then("I should see product created successfully")
    public void iShouldSeeProductCreatedSuccessfully() {
        var retrievedProduct = productService.getProduct(product.getId());
        assertThat(retrievedProduct).isNotNull();
        assertThat(product.getName()).isEqualTo(retrievedProduct.getName());
        assertThat(product.getPrice()).isEqualTo(retrievedProduct.getPrice());
        assertThat(product.getStockQuantity()).isEqualTo(retrievedProduct.getStockQuantity());
    }
}
