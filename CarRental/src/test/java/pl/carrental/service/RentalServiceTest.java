package pl.carrental.service;

import org.junit.jupiter.api.*;
import pl.carrental.BaseIntegrationTest;
import pl.carrental.client.Client;
import pl.carrental.client.ClientType;
import pl.carrental.vehicle.Car;
import pl.carrental.vehicle.Vehicle;

import static org.junit.jupiter.api.Assertions.*;

class RentalServiceTest extends BaseIntegrationTest {

    private RentalService rentalService;

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        rentalService = new RentalService(em);

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
    @DisplayName("should rent a vehicle when all conditions are met")
    void rentVehicle_shouldSucceed() {

        em.getTransaction().begin();
        Client client = new Client(1L, "Jan", "Kowalski", "jan@test.com", ClientType.STANDARD, 500.0);
        Vehicle vehicle = new Car(101L, "WX12345", "Ford", "Focus", 2022, "Niebieski", 100.0, 5);
        em.persist(client);
        em.persist(vehicle);
        em.getTransaction().commit();

        Rental rental = rentalService.rentVehicle(client.getClientId(), vehicle.getVehicleId(), 3);

        assertNotNull(rental);
        assertEquals(300.0, rental.getRentalPrice());

        Client clientAfter = em.find(Client.class, client.getId());
        Vehicle vehicleAfter = em.find(Vehicle.class, vehicle.getId());
        assertEquals(200.0, clientAfter.getBalance());
        assertTrue(vehicleAfter.isRented());
    }

    @Test
    @DisplayName("should throw exception when client has insufficient funds")
    void rentVehicle_shouldFailOnInsufficientFunds() {
        // GIVEN
        em.getTransaction().begin();
        Client client = new Client(1L, "Biedny", "Klient", "biedny@test.com", ClientType.STANDARD, 50.0);
        Vehicle vehicle = new Car(101L, "WX12345", "Ford", "Focus", 2022, "Niebieski", 100.0, 5);
        em.persist(client);
        em.persist(vehicle);
        em.getTransaction().commit();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            rentalService.rentVehicle(client.getClientId(), vehicle.getVehicleId(), 1);
        });

        assertTrue(exception.getMessage().contains("Niewystarczające środki na koncie"));
    }

    @Test
    @DisplayName("should successfully return a rented vehicle")
    void returnVehicle_shouldSucceed() {
        em.getTransaction().begin();
        Client client = new Client(1L, "Jan", "Kowalski", "jan@test.com", ClientType.STANDARD, 500.0);
        Vehicle vehicle = new Car(101L, "WX12345", "Ford", "Focus", 2022, "Niebieski", 100.0, 5);
        em.persist(client);
        em.persist(vehicle);
        em.getTransaction().commit();
        Rental activeRental = rentalService.rentVehicle(client.getClientId(), vehicle.getVehicleId(), 5);

        rentalService.returnVehicle(activeRental.getRentalId());

        Rental finishedRental = em.find(Rental.class, activeRental.getId());
        Vehicle vehicleAfterReturn = em.find(Vehicle.class, vehicle.getId());
        assertFalse(finishedRental.isActive());
        assertNotNull(finishedRental.getActualReturnDate());
        assertFalse(vehicleAfterReturn.isRented());
    }
}