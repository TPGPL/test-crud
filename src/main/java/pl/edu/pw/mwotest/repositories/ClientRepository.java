package pl.edu.pw.mwotest.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.edu.pw.mwotest.models.Client;

public interface ClientRepository extends CrudRepository<Client, Integer> {
}
