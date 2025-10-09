package pl.carrental.repository;

import pl.carrental.client.Client;
import java.util.*;

public interface ClientRepository {
    void save(Client client);
    Optional<Client> findById(Long id);
    Optional<Client> findByClientId(Long clientId);
    List<Client> findAll();
    void delete(Client client);
}
