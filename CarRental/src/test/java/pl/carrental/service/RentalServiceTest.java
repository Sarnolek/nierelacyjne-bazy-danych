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

    @Nested
    @DisplayName("Testy procesu wypożyczania (rentVehicle)")
    class RentVehicleTests {

        @Test
        @DisplayName("[Pozytywny] Powinien wypożyczyć pojazd, gdy wszystkie warunki są spełnione")
        void shouldRentVehicle_whenAllConditionsAreMet() {
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
        @DisplayName("[Brzegowy] Powinien wypożyczyć pojazd, gdy klient ma DOKŁADNIE tyle środków")
        void shouldRentVehicle_whenClientHasExactAmountOfMoney() {
            em.getTransaction().begin();
            Client client = new Client(1L, "Jan", "NaStyk", "styk@test.com", ClientType.STANDARD, 300.0);
            Vehicle vehicle = new Car(101L, "WX12345", "Ford", "Focus", 2022, "Niebieski", 100.0, 5);
            em.persist(client);
            em.persist(vehicle);
            em.getTransaction().commit();


            rentalService.rentVehicle(client.getClientId(), vehicle.getVehicleId(), 3);

            Client clientAfter = em.find(Client.class, client.getId());
            assertEquals(0.0, clientAfter.getBalance());
            assertTrue(em.find(Vehicle.class, vehicle.getId()).isRented());
        }

        @Test
        @DisplayName("[Negatywny] Powinien rzucić wyjątek dla nieistniejącego klienta")
        void shouldThrowException_forNonExistentClient() {
            long nonExistentClientId = 999L;
            em.getTransaction().begin();
            Vehicle vehicle = new Car(101L, "WX12345", "Ford", "Focus", 2022, "Niebieski", 100.0, 5);
            em.persist(vehicle);
            em.getTransaction().commit();


            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                rentalService.rentVehicle(nonExistentClientId, vehicle.getVehicleId(), 3);
            });
            assertTrue(exception.getMessage().contains("Nie znaleziono klienta o ID: " + nonExistentClientId));
        }

        @Test
        @DisplayName("[Negatywny] Powinien rzucić wyjątek dla nieistniejącego pojazdu")
        void shouldThrowException_forNonExistentVehicle() {
            long nonExistentVehicleId = 999L;
            em.getTransaction().begin();
            Client client = new Client(1L, "Jan", "Kowalski", "jan@test.com", ClientType.STANDARD, 500.0);
            em.persist(client);
            em.getTransaction().commit();

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                rentalService.rentVehicle(client.getClientId(), nonExistentVehicleId, 3);
            });
            assertTrue(exception.getMessage().contains("Nie znaleziono pojazdu o ID: " + nonExistentVehicleId));
        }

        @Test
        @DisplayName("[Negatywny] Powinien rzucić wyjątek dla niepoprawnej liczby dni")
        void shouldThrowException_forInvalidRentalDays() {
            em.getTransaction().begin();
            Client client = new Client(1L, "Jan", "Kowalski", "jan@test.com", ClientType.STANDARD, 500.0);
            Vehicle vehicle = new Car(101L, "WX12345", "Ford", "Focus", 2022, "Niebieski", 100.0, 5);
            em.persist(client);
            em.persist(vehicle);
            em.getTransaction().commit();

            int invalidDays = 0;
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                rentalService.rentVehicle(client.getClientId(), vehicle.getVehicleId(), invalidDays);
            });
            assertTrue(exception.getMessage().contains("Minimalny okres wypożyczenia to"));
        }

        @Test
        @DisplayName("[Integralność] Stan bazy nie powinien się zmienić po nieudanym wypożyczeniu (brak środków)")
        void databaseState_shouldNotChange_afterFailedRental() {
            double initialBalance = 100.0;
            em.getTransaction().begin();
            Client client = new Client(1L, "Jan", "Biedny", "biedny@test.com", ClientType.STANDARD, initialBalance);
            Vehicle vehicle = new Car(101L, "WX12345", "Ford", "Focus", 2022, "Niebieski", 150.0, 5);
            em.persist(client);
            em.persist(vehicle);
            em.getTransaction().commit();

            assertThrows(IllegalStateException.class, () -> {
                rentalService.rentVehicle(client.getClientId(), vehicle.getVehicleId(), 1);
            });


            Client clientAfter = em.find(Client.class, client.getId());
            Vehicle vehicleAfter = em.find(Vehicle.class, vehicle.getId());
            assertEquals(initialBalance, clientAfter.getBalance());
            assertFalse(vehicleAfter.isRented());
        }
    }

    @Nested
    @DisplayName("Testy procesu zwrotu (returnVehicle)")
    class ReturnVehicleTests {

        @Test
        @DisplayName("[Pozytywny] Powinien pomyślnie zwrócić wypożyczony pojazd")
        void shouldReturnVehicle_successfully() {
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


        @Test
        @DisplayName("[Negatywny] Powinien rzucić wyjątek dla nieistniejącego wypożyczenia")
        void shouldThrowException_forNonExistentRental() {

            long nonExistentRentalId = 999L;

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                rentalService.returnVehicle(nonExistentRentalId);
            });
            assertTrue(exception.getMessage().contains("Nie znaleziono wypożyczenia o ID: " + nonExistentRentalId));
        }
    }
}