package repository.mongo;

import db.MongoDbManager;
import model.Client;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientMongoRepositoryTest {

    private static ClientMongoRepository clientRepo;

    @BeforeAll
    static void setup() {
        // Łączy się z klastrem z docker-compose.yml
        MongoDbManager.init();
    }

    @AfterAll
    static void tearDown() {
        MongoDbManager.close();
    }

    @BeforeEach
    void setupEach() {
        // Czyści kolekcję przed każdym testem
        MongoDbManager.getDatabase().getCollection("clients").drop();
        clientRepo = new ClientMongoRepository();
    }

    @Test
    @Order(1)
    void testSaveAndFindById() {
        Client client = new Client("Jan", "Kowalski");
        clientRepo.save(client);
        System.out.println("Zapisano klienta: " + client.getId());

        Optional<Client> found = clientRepo.findById(client.getId());

        assertTrue(found.isPresent(), "Nie znaleziono klienta po ID");
        assertEquals("Jan", found.get().getFirstName());
        assertEquals("Kowalski", found.get().getLastName());
    }

    @Test
    @Order(2)
    void testSaveUpdatesExistingClient() {
        Client client = new Client("Anna", "Nowak");
        clientRepo.save(client);
        UUID id = client.getId();

        Client toUpdate = new Client("Anna (zmienione)", "Nowak-Zarębska");
        toUpdate.setId(id); // Ustawiamy to samo ID
        clientRepo.save(toUpdate); // Metoda save() działa jak upsert

        Optional<Client> found = clientRepo.findById(id);
        assertEquals(1, clientRepo.findAll().size(), "Powinien być tylko jeden klient");
        assertTrue(found.isPresent());
        assertEquals("Anna (zmienione)", found.get().getFirstName());
        assertEquals("Nowak-Zarębska", found.get().getLastName());
    }

    @Test
    @Order(3)
    void testFindAll() {
        assertEquals(0, clientRepo.findAll().size());

        clientRepo.save(new Client("Klient", "Jeden"));
        clientRepo.save(new Client("Klient", "Dwa"));

        List<Client> clients = clientRepo.findAll();
        assertEquals(2, clients.size());
    }

    @Test
    @Order(4)
    void testDeleteById() {
        Client client = new Client("Piotr", "DoUsunięcia");
        clientRepo.save(client);
        UUID id = client.getId();

        assertTrue(clientRepo.findById(id).isPresent(), "Klient powinien istnieć przed usunięciem");

        clientRepo.deleteById(id);

        assertFalse(clientRepo.findById(id).isPresent(), "Klient nie powinien istnieć po usunięciu");
    }
}