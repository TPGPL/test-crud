package pl.edu.pw.mwotest;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import pl.edu.pw.mwotest.models.Product;
import pl.edu.pw.mwotest.repositories.ProductRepository;
import pl.edu.pw.mwotest.services.ProductService;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class ProductServiceIntegrationTests {

    @Nested
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    @SpringBootTest
    abstract class ProductTestBase {
        @Autowired
        protected ProductRepository productRepository;
        @Autowired
        protected ProductService productService;
    }

    @Nested
    @Order(1)
    class CRUD_OperationTests extends ProductTestBase {
        @Test
        public void createProductSuccessful() {
            // given
            Product product = Product.builder().name("Piwo").price(2.99).stockQuantity(2136).build();

            // when
            var id = productService.createProduct(product).getId();

            // then
            Product retrievedProduct = productRepository.findById(id).orElse(null);
            assertNotNull(retrievedProduct);
            assertEquals(product.getName(), retrievedProduct.getName());
            assertEquals(product.getPrice(), retrievedProduct.getPrice());
            assertEquals(product.getStockQuantity(), retrievedProduct.getStockQuantity());
        }

        @Test
        public void createDuplicateNameProduct_shouldThrowException() {
            // given
            Product product = Product.builder().name("Piwo").price(2.99).stockQuantity(2136).build();
            Product product2 = Product.builder().name("Piwo").price(2.98).stockQuantity(2138).build();
            productRepository.save(product);

            // when / then
            assertThatThrownBy(() -> productService.createProduct(product2)).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        public void readProductByIdTestSuccess() {
            // given
            Product product = Product.builder().name("Piwo").price(2.99).stockQuantity(2136).build();
            var id = productRepository.save(product).getId();

            // when
            Product retrievedProduct = productService.getProduct(id);

            // then
            assertNotNull(retrievedProduct);
            assertEquals(product.getName(), retrievedProduct.getName());
            assertEquals(product.getPrice(), retrievedProduct.getPrice());
            assertEquals(product.getStockQuantity(), retrievedProduct.getStockQuantity());
        }

        @Test
        public void readNonExistentProductById() {
            // given
            var id = 1;

            // when
            Product retrievedProduct = productService.getProduct(id);

            // then
            assertNull(retrievedProduct);
        }

        @Test
        public void getAllTest() {
            // given
            Product product = Product.builder().name("Piwo").price(2.99).stockQuantity(2136).build();
            Product product2 = Product.builder().name("Wino").price(9.99).stockQuantity(2138).build();
            List<Product> products = List.of(product, product2);
            productRepository.saveAll(products);

            // when
            List<Product> receivedProducts = StreamSupport.stream(productService.getAllProducts().spliterator(), false).toList();

            // then
            assertEquals(receivedProducts, products);
        }

        @Test
        public void updateProductSuccessful() {
            // given
            Product oldProduct = Product.builder().name("Radler").price(21.36).stockQuantity(0).build();
            Product product = Product.builder().name("Piwo").price(0.99).stockQuantity(10).build();
            var id = productRepository.save(oldProduct).getId();

            // when
            productService.updateProduct(id, product);

            // then
            Product retrievedProduct = productRepository.findById(id).orElse(null);
            assertNotNull(retrievedProduct);
            assertEquals(product.getName(), retrievedProduct.getName());
            assertEquals(product.getPrice(), retrievedProduct.getPrice());
            assertEquals(product.getStockQuantity(), retrievedProduct.getStockQuantity());
        }

        @Test
        public void updateNonExistentProduct() {
            // given
            Product product = Product.builder().name("Piwo").price(0.99).stockQuantity(0).build();
            var id = 20000;

            // when / then
            assertThatThrownBy(() -> productService.updateProduct(id, product)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void deleteSuccessful() {
            // given
            Product product = Product.builder().name("Piwo").price(0.99).stockQuantity(0).build();
            product = productRepository.save(product);

            // when
            productService.deleteProduct(product.getId());

            // then
            assertTrue(productRepository.findById(product.getId()).isEmpty());
        }
    }

    @Nested
    @Order(2)
    class CreateConstraintsTests extends ProductTestBase {
        @Test
        public void testCreateProductWithTooShortName() {
            Product product = Product.builder().name("a").price(0.99).stockQuantity(0).build();

            assertThatThrownBy(() -> productService.createProduct(product)).isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        public void testCreateProductWithTooLongName() {
            String tooLongName = "a".repeat(Product.MAX_NAME_LENGTH + 1);
            Product product = Product.builder().name(tooLongName).price(0.99).stockQuantity(0).build();

            assertThatThrownBy(() -> productService.createProduct(product)).isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        public void testCreateProductWithInvalidPrice() {
            Product product = Product.builder().name("ValidName").price(-1.2).stockQuantity(0).build();

            assertThatThrownBy(() -> productService.createProduct(product)).isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        public void testCreateProductWithInvalidStockQuantity() {
            Product product = Product.builder().name("ValidName").price(0.99).stockQuantity(-1).build();

            assertThatThrownBy(() -> productService.createProduct(product)).isInstanceOf(ConstraintViolationException.class);
        }
    }

    @Nested
    @Order(3)
    class UpdateConstraintsTests extends ProductTestBase {

        @Test
        public void testUpdateProductWithTooShortName() {
            // given
            Product validProduct = Product.builder().name("ValidName").price(0.99).stockQuantity(0).build();
            validProduct = productRepository.save(validProduct);

            // when
            validProduct.setName("a");

            // then
            Product finalValidProduct = validProduct;
            assertThatThrownBy(() -> productService.updateProduct(finalValidProduct.getId(), finalValidProduct)).isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        public void testUpdateProductWithTooLongName() {
            // given
            Product validProduct = Product.builder().name("ValidName").price(0.99).stockQuantity(0).build();
            validProduct = productRepository.save(validProduct);

            // when
            String tooLongName = "a".repeat(Product.MAX_NAME_LENGTH + 1);
            validProduct.setName(tooLongName);

            // then
            Product finalValidProduct = validProduct;
            assertThatThrownBy(() -> productService.updateProduct(finalValidProduct.getId(), finalValidProduct)).isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        public void testUpdateProductWithInvalidPrice() {
            // given
            Product validProduct = Product.builder().name("ValidName").price(0.99).stockQuantity(0).build();
            validProduct = productRepository.save(validProduct);

            // when
            validProduct.setPrice(0);

            // then
            Product finalValidProduct = validProduct;
            assertThatThrownBy(() -> productService.updateProduct(finalValidProduct.getId(), finalValidProduct)).isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        public void testUpdateProductWithInvalidStockQuantity() {
            // given
            Product validProduct = Product.builder().name("ValidName").price(0.99).stockQuantity(0).build();
            validProduct = productRepository.save(validProduct);

            // when
            validProduct.setStockQuantity(-2);

            // then
            Product finalValidProduct = validProduct;
            assertThatThrownBy(() -> productService.updateProduct(finalValidProduct.getId(), finalValidProduct)).isInstanceOf(ConstraintViolationException.class);
        }
    }
}