package pl.carrental.implemenation;

import org.junit.jupiter.api.*;
import pl.carrental.BaseIntegrationTest;
import pl.carrental.repository.VehicleRepository;
import pl.carrental.vehicle.Car;
import pl.carrental.vehicle.Truck;
import pl.carrental.vehicle.Vehicle;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class VehicleRepositoryImplTest extends BaseIntegrationTest {

    private VehicleRepository vehicleRepository;

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        vehicleRepository = new VehicleRepositoryImpl(em);

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
    void saveAndFindAll_shouldWorkForSubtypes() {
        Vehicle car = new Car(1L, "CAR1", "BMW", "3", 2020, "Szary", 200.0, 5);
        Vehicle truck = new Truck(2L, "TRUCK1", "Scania", "R", 2019, "Czerwony", 400.0, 20000.0);
        em.getTransaction().begin();
        vehicleRepository.save(car);
        vehicleRepository.save(truck);
        em.getTransaction().commit();

        List<Vehicle> allVehicles = vehicleRepository.findAll();
        Optional<Vehicle> foundCar = vehicleRepository.findByPlateNumber("CAR1");
        Optional<Vehicle> foundTruck = vehicleRepository.findByVehicleId(2L);

        assertEquals(2, allVehicles.size());
        assertTrue(foundCar.isPresent());
        assertTrue(foundCar.get() instanceof Car);
        assertTrue(foundTruck.isPresent());
        assertTrue(foundTruck.get() instanceof Truck);
    }
}