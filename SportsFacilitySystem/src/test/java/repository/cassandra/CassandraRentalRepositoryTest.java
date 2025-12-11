package repository.cassandra;

import model.Rental;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class CassandraRentalRepositoryTest extends BaseCassandraTest {

    private CassandraRentalRepository rentalRepo;

    @BeforeEach
    void setUp() {
        session.execute("TRUNCATE rentals_by_client");
        session.execute("TRUNCATE rentals_by_facility");
        session.execute("TRUNCATE rentals_by_id");

        rentalRepo = new CassandraRentalRepository(mapper.rentalDao(), session);
    }

    @Test
    void shouldSaveAndFindById() {
        UUID clientId = UUID.randomUUID();
        UUID facilityId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now().withNano(0);
        LocalDateTime end = start.plusHours(2);
        Rental rental = new Rental(clientId, facilityId, start, end);

        rentalRepo.save(rental);

        Rental found = rentalRepo.findById(rental.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(rental.getId(), found.getId());
        assertEquals(clientId, found.getClientId());
        assertEquals(facilityId, found.getFacilityId());
    }

    @Test
    void shouldFindByClientId() {
        UUID clientId = UUID.randomUUID();
        UUID facilityId1 = UUID.randomUUID();
        UUID facilityId2 = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now().withNano(0);

        Rental rental1 = new Rental(clientId, facilityId1, start, start.plusHours(1));
        Rental rental2 = new Rental(clientId, facilityId2, start.plusHours(2), start.plusHours(3));

        rentalRepo.save(rental1);
        rentalRepo.save(rental2);

        List<Rental> rentals = rentalRepo.findByClientId(clientId);


        assertEquals(2, rentals.size());

        assertTrue(rentals.stream().anyMatch(r -> r.getId().equals(rental1.getId())));
        assertTrue(rentals.stream().anyMatch(r -> r.getId().equals(rental2.getId())));
    }

    @Test
    void shouldFindByFacilityId() {
        UUID facilityId = UUID.randomUUID();
        UUID clientId1 = UUID.randomUUID();
        UUID clientId2 = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now().withNano(0);

        Rental rental1 = new Rental(clientId1, facilityId, start, start.plusHours(1));
        Rental rental2 = new Rental(clientId2, facilityId, start.plusHours(2), start.plusHours(3));

        rentalRepo.save(rental1);
        rentalRepo.save(rental2);


        List<Rental> rentals = rentalRepo.findByFacilityId(facilityId);

        assertEquals(2, rentals.size());
        assertTrue(rentals.stream().anyMatch(r -> r.getId().equals(rental1.getId())));
        assertTrue(rentals.stream().anyMatch(r -> r.getId().equals(rental2.getId())));
    }

    @Test
    void shouldDeleteFromAllTables() {
        UUID clientId = UUID.randomUUID();
        UUID facilityId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now().withNano(0);
        Rental rental = new Rental(clientId, facilityId, start, start.plusHours(1));

        rentalRepo.save(rental);
        assertTrue(rentalRepo.findById(rental.getId()).isPresent());


        rentalRepo.deleteById(rental.getId());


        assertTrue(rentalRepo.findById(rental.getId()).isEmpty());

        List<Rental> clientRentals = rentalRepo.findByClientId(clientId);
        assertTrue(clientRentals.isEmpty(), "Rezerwacja powinna zniknąć z tabeli klienta");

        List<Rental> facilityRentals = rentalRepo.findByFacilityId(facilityId);
        assertTrue(facilityRentals.isEmpty(), "Rezerwacja powinna zniknąć z tabeli obiektu");
    }
}