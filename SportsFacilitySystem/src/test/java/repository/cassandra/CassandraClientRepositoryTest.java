package repository.cassandra;

import model.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class CassandraClientRepositoryTest extends BaseCassandraTest {

    private CassandraClientRepository clientRepo;

    @BeforeEach
    void setUp() {
        session.execute("TRUNCATE clients");
        clientRepo = new CassandraClientRepository(mapper.clientDao());
    }

    @Test
    void shouldSaveAndFindClientById() {

        Client client = new Client("Adam", "Małysz");


        clientRepo.save(client);
        Client found = clientRepo.findById(client.getId()).orElse(null);


        assertNotNull(found);
        assertEquals(client.getId(), found.getId());
        assertEquals("Adam", found.getFirstName());
        assertEquals("Małysz", found.getLastName());
    }

    @Test
    void shouldDeleteClient() {
        Client client = new Client("Robert", "Kubica");
        clientRepo.save(client);
        assertTrue(clientRepo.findById(client.getId()).isPresent());

        clientRepo.deleteById(client.getId());

        assertTrue(clientRepo.findById(client.getId()).isEmpty());
    }

    @Test
    void findAll_ShouldThrowException() {
        assertThrows(UnsupportedOperationException.class, () -> clientRepo.findAll());
    }
}