package pl.edu.pw.mwotest.services;

import jakarta.validation.ConstraintViolationException;
import net.bytebuddy.dynamic.DynamicType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.edu.pw.mwotest.dtos.ProductDto;
import pl.edu.pw.mwotest.models.Client;
import pl.edu.pw.mwotest.models.Product;
import pl.edu.pw.mwotest.repositories.ProductRepository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ProductServiceTest {
    @Autowired
    private ProductService service;

    @MockBean
    private ProductRepository repository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void cleanup() {
        Mockito.reset(repository);
    }

    @ParameterizedTest
    @MethodSource("getInvalidData")
    public void createProduct_forInvalidInput_throwsException(String name,
                                                              double price,
                                                              int stock) {
        // arrange
        ProductDto product = new ProductDto();
        product.setName(name);
        product.setPrice(price);
        product.setStockQuantity(stock);

        assertThrows(ConstraintViolationException.class, ()-> {
           // act
           Product result = service.createProduct(product);
        });
    }

    @Test
    public void createProduct_forValidInput_ReturnsNewProduct() {

    }

    @Test
    public void updateProduct_forValidInput_ReturnsUpdatedProduct() {
        // arrange
        Product old = new Product(2,"Old",12.3,12,new ArrayList<>());
        ProductDto newProd = new ProductDto();
        newProd.setName("New");
        newProd.setPrice(11);
        newProd.setStockQuantity(10);

        Mockito.when(repository.findById(2)).thenReturn(Optional.of(old));
        Mockito.when(repository.save(any(Product.class))).thenReturn(old);

        // act
        Product result = service.updateProduct(2,newProd);

        // assert
        assertThat(result.getName()).isEqualTo("New");
        assertThat(result.getPrice()).isEqualTo(11);
        assertThat(result.getStockQuantity()).isEqualTo(10);
    }

    static Stream<Arguments> getInvalidData() {
        return Stream.of(
                Arguments.of("a", 2.4, 14), // too short name
                Arguments.of("name", 0, 12), // price = 0
                Arguments.of("test", 1.2, -1) // negative stock
        );
    }
}