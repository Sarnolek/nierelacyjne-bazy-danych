import com.datastax.oss.driver.api.core.CqlSession;
import config.CassandraSessionManager;
import model.Client;
import model.Rental;
import model.SportsFacility;
import model.TennisCourt;
import model.SurfaceType;
import repository.ClientRepository;
import repository.RentalRepository;
import repository.SportsFacilityRepository;
import repository.cassandra.CassandraClientRepository;
import repository.cassandra.CassandraRentalRepository;
import repository.cassandra.CassandraSportsFacilityRepository;
import repository.cassandra.ClientDao;
import repository.cassandra.RentalDao;
import repository.cassandra.SportsFacilityDao;
import repository.cassandra.SportsFacilityMapper;
import service.RentalException;
import service.RentalService;
import service.RentalServiceImpl;

import java.time.LocalDateTime;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        CqlSession session = CassandraSessionManager.getSession();
        SportsFacilityMapper mapper = SportsFacilityMapper.builder(session).build();
        ClientDao clientDao = mapper.clientDao();
        SportsFacilityDao facilityDao = mapper.sportsFacilityDao();
        RentalDao rentalDao = mapper.rentalDao();

        ClientRepository clientRepo = new CassandraClientRepository(clientDao);
        SportsFacilityRepository facilityRepo = new CassandraSportsFacilityRepository(facilityDao);
        RentalRepository rentalRepo = new CassandraRentalRepository(rentalDao, session);

        RentalService rentalService = new RentalServiceImpl(clientRepo, facilityRepo, rentalRepo);

        Client client1 = new Client("Karol", "Dawid");
        clientRepo.save(client1);
        System.out.println("[CREATE] Zapisano klienta: " + client1.getFirstName() + " " + client1.getLastName() + " ID: " + client1.getId());

        SportsFacility court1 = new TennisCourt("Kort Centralny", 50.0, 4, SurfaceType.CLAY, true);
        facilityRepo.save(court1);
        System.out.println("[CREATE] Zapisano obiekt: " + court1.getName() + " ID: " + court1.getId());

        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(2);

        try {
            Rental rental = rentalService.rentFacility(client1.getId(), court1.getId(), start, end);
        } catch (RentalException e) {
        }

        var clientRentals = rentalService.getRentalsForClient(client1.getId());
        if (!clientRentals.isEmpty()) {
            clientRentals.forEach(r -> System.out.println(" -> Znaleziono rezerwację w historii klienta: ID obiektu=" + r.getFacilityId()));
        } else {
            System.err.println(" -> BŁĄD: Nie znaleziono rezerwacji w tabeli klienta!");
        }

        var facilityRentals = rentalService.getRentalsForFacility(court1.getId());
        if (!facilityRentals.isEmpty()) {
            facilityRentals.forEach(r -> System.out.println(" -> Znaleziono rezerwację w kalendarzu obiektu: ID klienta=" + r.getClientId()));
        } else {
            System.err.println(" -> BŁĄD: Nie znaleziono rezerwacji w tabeli obiektu!");
        }

        try {
            System.out.println("\n Test kolizji (próba rezerwacji zajętego terminu)...");
            LocalDateTime conflictStart = start.plusHours(1);
            LocalDateTime conflictEnd = conflictStart.plusHours(2);

            rentalService.rentFacility(client1.getId(), court1.getId(), conflictStart, conflictEnd);
        } catch (RentalException e) {
            System.out.println("[SUCCESS] Oczekiwany błąd: " + e.getMessage());
        }

        CassandraSessionManager.closeSession();
        System.exit(0);
    }
}