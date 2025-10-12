package pl.carrental.implemenation;

import org.junit.jupiter.api.*;
import pl.carrental.BaseIntegrationTest;
import pl.carrental.client.Client;
import pl.carrental.client.ClientType;
import pl.carrental.repository.ClientRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ClientRepositoryImplTest extends BaseIntegrationTest {

    private ClientRepository clientRepository;

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        clientRepository = new ClientRepositoryImpl(em);

        em.getTransaction().begin();
        em.createQuery("DELETE FROM Rental").executeUpdate();
        em.createQuery("DELETE FROM Client").executeUpdate();
        em.createQuery("DELETE FROM Vehicle").executeUpdate();
        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    @Test
    @DisplayName("should save a new client to the database")
    void save_shouldPersistNewClient() {
        Client newClient = new Client(1L, "Tomasz", "Kot", "tomasz.kot@test.com", ClientType.STANDARD, 200.0);

        em.getTransaction().begin();
        clientRepository.save(newClient);
        em.getTransaction().commit();

        Client foundClient = em.find(Client.class, newClient.getId());
        assertNotNull(foundClient);
        assertEquals("Tomasz", foundClient.getFirstName());
    }

    @Test
    @DisplayName("should find a client by its business ID (clientId)")
    void findByClientId_shouldReturnCorrectClient() {
        long businessId = 99L;
        Client client = new Client(businessId, "Agata", "Buzek", "agata.buzek@test.com", ClientType.BUSINESS, 10000.0);
        em.getTransaction().begin();
        em.persist(client);
        em.getTransaction().commit();

        Optional<Client> foundClient = clientRepository.findByClientId(businessId);

        assertTrue(foundClient.isPresent());
        assertEquals("Agata", foundClient.get().getFirstName());
    }

    @Test
    @DisplayName("should delete a client from the database")
    void delete_shouldRemoveClient() {
        Client clientToDelete = new Client(1L, "Test", "Delete", "delete@test.com", ClientType.STANDARD, 0.0);
        em.getTransaction().begin();
        em.persist(clientToDelete);
        em.getTransaction().commit();

        em.getTransaction().begin();
        clientRepository.delete(clientToDelete);
        em.getTransaction().commit();

        Client foundClient = em.find(Client.class, clientToDelete.getId());
        assertNull(foundClient);
    }
}