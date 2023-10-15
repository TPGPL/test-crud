package pl.edu.pw.mwotest.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.edu.pw.mwotest.models.Order;

public interface OrderRepository extends CrudRepository<Order, Integer> {
}
