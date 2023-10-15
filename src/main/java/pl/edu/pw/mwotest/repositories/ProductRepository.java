package pl.edu.pw.mwotest.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.edu.pw.mwotest.models.Product;

public interface ProductRepository extends CrudRepository<Product, Integer> {
}
