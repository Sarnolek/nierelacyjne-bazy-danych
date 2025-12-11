package service;

import model.Client;
import model.Rental;
import model.SportsFacility;
import model.TennisCourt;
import model.SurfaceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.cassandra.BaseCassandraTest;
import repository.cassandra.CassandraClientRepository;
import repository.cassandra.CassandraRentalRepository;
import repository.cassandra.CassandraSportsFacilityRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RentalServiceIntegrationTest extends BaseCassandraTest {

    private CassandraClientRepository clientRepo;
    private CassandraSportsFacilityRepository facilityRepo;
    private CassandraRentalRepository rentalRepo;
    private RentalService rentalService;

    @BeforeEach
    void setUp() {
        session.execute("TRUNCATE clients");
        session.execute("TRUNCATE facilities");
        session.execute("TRUNCATE rentals_by_client");
        session.execute("TRUNCATE rentals_by_facility");
        session.execute("TRUNCATE rentals_by_id");

        clientRepo = new CassandraClientRepository(mapper.clientDao());
        facilityRepo = new CassandraSportsFacilityRepository(mapper.sportsFacilityDao());
        rentalRepo = new CassandraRentalRepository(mapper.rentalDao(), session);

        rentalService = new RentalServiceImpl(clientRepo, facilityRepo, rentalRepo);
    }

    @Test
    void shouldRentFacilitySuccessfully() throws RentalException {
        Client client = new Client("Jan", "Kowalski");
        clientRepo.save(client);

        SportsFacility facility = new TennisCourt("Kort 1", 50.0, 2, SurfaceType.GRASS, false);
        facilityRepo.save(facility);

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);

        Rental rental = rentalService.rentFacility(client.getId(), facility.getId(), start, end);

        assertNotNull(rental);
        assertEquals(client.getId(), rental.getClientId());
        assertEquals(facility.getId(), rental.getFacilityId());

        List<Rental> storedRentals = rentalService.getRentalsForClient(client.getId());
        assertFalse(storedRentals.isEmpty());
        assertEquals(rental.getId(), storedRentals.get(0).getId());
    }

    @Test
    void shouldThrowExceptionWhenEndTimeBeforeStartTime() {
        UUID clientId = UUID.randomUUID();
        UUID facilityId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = start.minusHours(1);

        assertThrows(RentalException.class, () -> rentalService.rentFacility(clientId, facilityId, start, end));
    }

    @Test
    void shouldThrowExceptionWhenRentingInPast() {
        UUID clientId = UUID.randomUUID();
        UUID facilityId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now().minusHours(5);
        LocalDateTime end = start.plusHours(1);

        assertThrows(RentalException.class, () -> rentalService.rentFacility(clientId, facilityId, start, end));
    }

    @Test
    void shouldThrowExceptionWhenClientNotFound() {
        SportsFacility facility = new TennisCourt("Kort 2", 60.0, 4, SurfaceType.CLAY, true);
        facilityRepo.save(facility);

        UUID nonExistentClientId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);

        assertThrows(RentalException.class, () -> rentalService.rentFacility(nonExistentClientId, facility.getId(), start, end));
    }

    @Test
    void shouldThrowExceptionWhenFacilityNotFound() {
        Client client = new Client("Anna", "Nowak");
        clientRepo.save(client);

        UUID nonExistentFacilityId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);

        assertThrows(RentalException.class, () -> rentalService.rentFacility(client.getId(), nonExistentFacilityId, start, end));
    }

    @Test
    void shouldThrowExceptionWhenFacilityIsAlreadyRented() throws RentalException {
        Client client = new Client("Piotr", "Zieliński");
        clientRepo.save(client);

        SportsFacility facility = new TennisCourt("Kort Główny", 120.0, 10, SurfaceType.HARD, true);
        facilityRepo.save(facility);

        LocalDateTime start1 = LocalDateTime.now().plusHours(10);
        LocalDateTime end1 = start1.plusHours(2);
        rentalService.rentFacility(client.getId(), facility.getId(), start1, end1);

        LocalDateTime start2 = start1.plusMinutes(30);
        LocalDateTime end2 = start2.plusHours(2);

        assertThrows(RentalException.class, () -> rentalService.rentFacility(client.getId(), facility.getId(), start2, end2));
    }
}