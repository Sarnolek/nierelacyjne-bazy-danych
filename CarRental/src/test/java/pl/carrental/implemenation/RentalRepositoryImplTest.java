package pl.carrental.implemenation;

import org.junit.jupiter.api.*;
import pl.carrental.BaseIntegrationTest;
import pl.carrental.client.Client;
import pl.carrental.client.ClientType;
import pl.carrental.repository.RentalRepository;
import pl.carrental.service.Rental;
import pl.carrental.vehicle.Car;
import pl.carrental.vehicle.Vehicle;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RentalRepositoryImplTest extends BaseIntegrationTest {

    private RentalRepository rentalRepository;
    private Client testClient;
    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        rentalRepository = new RentalRepositoryImpl(em);

        em.getTransaction().begin();
        em.createQuery("DELETE FROM Rental").executeUpdate();
        em.createQuery("DELETE FROM Client").executeUpdate();
        em.createQuery("DELETE FROM Vehicle").executeUpdate();

        testClient = new Client(1L, "Test", "Client", "client@test.com", ClientType.STANDARD, 1000.0);
        testVehicle = new Car(101L, "TEST_CAR", "Test", "Car", 2023, "Test", 100.0, 5);
        em.persist(testClient);
        em.persist(testVehicle);

        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    @Test
    void save_shouldPersistNewRental() {
        // GIVEN
        Rental newRental = new Rental(501L, testClient, testVehicle, 5);

        // WHEN
        em.getTransaction().begin();
        rentalRepository.save(newRental);
        em.getTransaction().commit();

        // THEN
        Rental foundRental = em.find(Rental.class, newRental.getId());
        assertNotNull(foundRental);
        assertEquals(testClient.getId(), foundRental.getClient().getId());
        assertEquals(testVehicle.getId(), foundRental.getVehicle().getId());
    }

    @Test
    void findByClient_shouldReturnClientRentals() {
        // GIVEN
        Rental rental1 = new Rental(601L, testClient, testVehicle, 2);
        em.getTransaction().begin();
        rentalRepository.save(rental1);
        em.getTransaction().commit();

        // WHEN
        List<Rental> clientRentals = rentalRepository.findByClient(testClient);

        // THEN
        assertEquals(1, clientRentals.size());
        assertEquals(rental1.getId(), clientRentals.get(0).getId());
    }
}