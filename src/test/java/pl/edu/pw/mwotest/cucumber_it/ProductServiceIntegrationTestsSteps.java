package pl.edu.pw.mwotest.cucumber_it;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.ThrowableAssert;
import org.springframework.dao.DataIntegrityViolationException;
import pl.edu.pw.mwotest.models.Product;
import pl.edu.pw.mwotest.services.ProductService;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RequiredArgsConstructor
public class ProductServiceIntegrationTestsSteps {
    private static Product product;
    private static ThrowableAssert.ThrowingCallable actionToCall;
    private static int requestedId;
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
                .orderLines(new ArrayList<>())
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

    @When("I try to create a product")
    public void iTryToCreateAProduct() {
        actionToCall = () -> productService.createProduct(product);
    }

    @Then("product creation throws Validation Exception")
    public void productCreationThrowsValidationException() {
        assertThatThrownBy(actionToCall).isInstanceOf(ConstraintViolationException.class);
    }


    @And("product is created if not exists")
    public void productIsCreatedIfNotExists() {
        try {
            requestedId = productService.createProduct(product).getId();
        } catch (DataIntegrityViolationException ignored) {
            Optional<Product> existingProduct = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                    .filter(existing -> existing.getName().equals(product.getName()))
                    .findFirst();

            existingProduct.ifPresentOrElse(
                    existing -> requestedId = existing.getId(),
                    () -> {
                        throw new RuntimeException("Product not found");
                    }
            );
        }
    }

    @When("I create product with duplicated name")
    public void iCreateProductWithDuplicatedName() {
        var product2 = Product.builder()
                .name(product.getName())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .orderLines(new ArrayList<>())
                .build();
        actionToCall = () -> productService.createProduct(product2);
    }

    @Then("product creation throws Unique Constraint Exception")
    public void productCreationThrowsUniqueConstraintException() {
        assertThatThrownBy(actionToCall).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Given("product id is max+{int}")
    public void productIdIsMax(int arg0) {
        Stream<Integer> productIDs = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                .map(Product::getId);
        int maxProductId = productIDs.max(Comparator.naturalOrder()).orElse(0);
        requestedId = maxProductId + arg0;
    }

    @When("I read product by id")
    public void iReadProductById() {
        product = productService.getProduct(requestedId);
    }

    @Then("product is null")
    public void productIsNull() {
        assertThat(product).isNull();
    }

    @When("I read product")
    public void iReadProduct() {
        productService.getProduct(requestedId);
    }

    @Then("I should see product data as below")
    public void iShouldSeeProductDataAsBelow(List<Map<String, String>> data) {
        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo(proxyBlank(data.get(0).get("name")));
        assertThat(product.getPrice()).isEqualTo(proxyZero(data.get(0).get("price"), Double.class).doubleValue());
        assertThat(product.getStockQuantity()).isEqualTo(proxyZero(data.get(0).get("stockQuantity"), Integer.class).intValue());
    }

    @When("I update product by id")
    public void iUpdateProductById() {
        actionToCall = () -> productService.updateProduct(
                requestedId, Product.builder().name("NewName").price(102.0).stockQuantity(1).build()
        );
    }

    @Then("product not found exception is thrown")
    public void productNotFoundExceptionIsThrown() {
        assertThatThrownBy(actionToCall).isInstanceOf(IllegalArgumentException.class);
    }

    @When("I update product with data below")
    public void iUpdateProductWithDataBelow(List<Map<String, String>> data) {
        String upName = proxyBlank(data.get(0).get("name"));
        double upPrice = proxyZero(data.get(0).get("price"), Double.class).doubleValue();
        int upStockQuantity = proxyZero(data.get(0).get("stockQuantity"), Integer.class).intValue();

        product = productService.updateProduct(
                requestedId, Product.builder().name(upName).price(upPrice).stockQuantity(upStockQuantity).build()
        );
    }

    @When("I delete product")
    public void iDeleteProduct() {
        productService.deleteProduct(requestedId);
    }

    @Then("product is not in database anymore")
    public void productIsNotInDatabaseAnymore() {
        assertThat(productService.getProduct(requestedId)).isNull();
    }
}
