import model.*;
import repository.*;
import service.RentalException;
import service.RentalService;
import service.RentalServiceImpl;

import java.time.LocalDateTime;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        ClientRepository clientRepo = new InMemoryClientRepository();
        SportsFacilityRepository facilityRepo = new InMemorySportsFacilityRepository();
        RentalRepository rentalRepo = new InMemoryRentalRepository();
        RentalService rentalService = new RentalServiceImpl(clientRepo, facilityRepo, rentalRepo);

        Client client1 = new Client("Karol", "Dawid");
        clientRepo.save(client1);
        System.out.println("Utworzono klienta: " + client1.getId());

        SportsFacility court1 = new TennisCourt("Kort boczny", 45, 4, SurfaceType.CLAY, true);
        facilityRepo.save(court1);
        System.out.println("Utworzono kort: " + court1.getId());

        LocalDateTime start = LocalDateTime.of(2025, 11, 1, 21, 0);
        LocalDateTime end = LocalDateTime.of(2025, 11, 1, 22, 0);

        try {
            System.out.println("Próba rezerwacji");
            Rental rezerwacja = rentalService.rentFacility(client1.getId(), court1.getId(), start, end);
        } catch (RentalException e) {
            System.out.println("Bład: " + e.getMessage());
        }

        try {
            System.out.println("Proba skolidowania");
            Rental rezerwacja_2 = rentalService.rentFacility(client1.getId(), court1.getId(), start, end);
        } catch (RentalException e) {
            System.out.println("Blad, doszlo do kolizji" + e.getMessage());
        }

        try {
            System.out.println("Fejkowy klient");
            UUID fakeClientId = UUID.randomUUID();
            rentalService.rentFacility(fakeClientId, court1.getId(), start.plusDays(1), end.plusDays(1));
        } catch (RentalException e) {
            System.out.println("Blad, klient nie istnieje: " + e.getMessage());
        }
    }
}
