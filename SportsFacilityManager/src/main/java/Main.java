// W pliku: SportsFacilityManager/src/main/java/Main.java

import com.mongodb.MongoCommandException;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationOptions;
import db.MongoDbManager;
import model.*; // Importuje wszystkie modele
import org.bson.Document;
import repository.ClientRepository;
import repository.RentalRepository;
import repository.SportsFacilityRepository;
import repository.mongo.ClientMongoRepository;
import repository.mongo.RentalMongoRepository;
import repository.mongo.SportsFacilityMongoRepository;
import service.RentalException;
import service.RentalService;
import service.RentalServiceImpl;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        // === 1. INICJALIZACJA POŁĄCZENIA ===
        MongoDbManager.init();
        System.out.println("Nawiązano połączenie z bazą danych.");

        // === 2. CZYSZCZENIE I PRZYGOTOWANIE BAZY DANYCH ===
        try {
            System.out.println("Czyszczenie starych danych...");
            MongoDbManager.getDatabase().getCollection("facilities").drop();
            MongoDbManager.getDatabase().getCollection("clients").drop();
            MongoDbManager.getDatabase().getCollection("rentals").drop();
            System.out.println("Kolekcje wyczyszczone.");
        } catch (Exception e) {
            System.out.println("Brak kolekcji do wyczyszczenia, kontynuuję...");
        }

        // === 3. WALIDACJA SCHEMATU (Nadal aktywna) ===
        try {
            Document schema = Document.parse("{" +
                    "  $jsonSchema: {" +
                    "    bsonType: 'object'," +
                    "    properties: {" +
                    "      is_rented: {" +
                    "        bsonType: 'int'," +
                    "        minimum: 0," +
                    "        maximum: 1," + // KLUCZOWA REGUŁA
                    "        description: 'musi byc 0 (wolny) lub 1 (zajety)'" +
                    "      }" +
                    "    }" +
                    "  }" +
                    "}");
            ValidationOptions validationOptions = new ValidationOptions()
                    .validator(schema)
                    .validationAction(ValidationAction.ERROR);
            MongoDbManager.getDatabase().createCollection(
                    "facilities",
                    new CreateCollectionOptions().validationOptions(validationOptions)
            );
            System.out.println("Kolekcja 'facilities' stworzona z walidacją.");
        } catch (MongoCommandException e) {
            if (e.getErrorCode() == 48) { // NamespaceExists
                System.out.println("Kolekcja 'facilities' już istnieje. Walidacja powinna być aktywna.");
            } else {
                System.err.println("Błąd przy tworzeniu kolekcji: " + e.getMessage());
            }
        }

        System.out.println("--- Inicjalizacja zakończona. Wypełnianie bazy danymi... ---");

        // === 4. INICJALIZACJA REPOZYTORIÓW I SERWISU ===
        ClientRepository clientRepo = new ClientMongoRepository();
        SportsFacilityRepository facilityRepo = new SportsFacilityMongoRepository();
        RentalRepository rentalRepo = new RentalMongoRepository();
        RentalService rentalService = new RentalServiceImpl(clientRepo, facilityRepo, rentalRepo);

        // === 5. TWORZENIE KLIENTÓW ===
        System.out.println("\n--- Tworzenie Klientów ---");
        Client client1 = new Client("Jan", "Kowalski");
        Client client2 = new Client("Anna", "Nowak");
        Client client3 = new Client("Piotr", "Wiśniewski");

        clientRepo.save(client1);
        clientRepo.save(client2);
        clientRepo.save(client3);
        System.out.println("Dodano 3 klientów do kolekcji 'clients'.");

        // === 6. TWORZENIE OBIEKTÓW SPORTOWYCH (POLIMORFIZM) ===
        System.out.println("\n--- Tworzenie Obiektów Sportowych ---");

        SportsFacility gym = new Gym("Siłownia Gold", 50.0, 20, 300, true);
        SportsFacility pool = new SwimmingPool("Pływalnia Fala", 80.0, 50, 25, 6);
        SportsFacility court = new TennisCourt("Korty Rakieta", 65.0, 4, SurfaceType.CLAY, false);

        facilityRepo.save(gym);
        facilityRepo.save(pool);
        facilityRepo.save(court);

        System.out.println("Dodano 3 obiekty (Gym, SwimmingPool, TennisCourt) do kolekcji 'facilities'.");
        System.out.println("Sprawdź pole '_t' w MongoDB Compass, aby zobaczyć dyskryminator.");
        System.out.println(" -> " + gym.getName() + " (is_rented: " + gym.getIsRented() + ")");
        System.out.println(" -> " + pool.getName() + " (is_rented: " + pool.getIsRented() + ")");
        System.out.println(" -> " + court.getName() + " (is_rented: " + court.getIsRented() + ")");

        // === 7. TWORZENIE REZERWACJI (ŁĄCZENIE DANYCH) ===
        System.out.println("\n--- Tworzenie Rezerwacji ---");
        LocalDateTime start1 = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        LocalDateTime end1 = start1.plusHours(2); // 10:00 - 12:00

        LocalDateTime start2 = LocalDateTime.now().plusDays(2).withHour(14).withMinute(0);
        LocalDateTime end2 = start2.plusHours(1); // 14:00 - 15:00

        LocalDateTime start3 = LocalDateTime.now().plusDays(3).withHour(10).withMinute(0);
        LocalDateTime end3 = start3.plusHours(1); // 10:00 - 11:00

        try {
            // Rezerwacja 1: Jan Kowalski na Siłownię Gold
            Rental rental1 = rentalService.rentFacility(client1.getId(), gym.getId(), start1, end1);
            System.out.println("✅ Stworzono rezerwację 1: " + client1.getFirstName() + " na " + gym.getName());

            // Rezerwacja 2: Anna Nowak na Pływalnię Fala
            Rental rental2 = rentalService.rentFacility(client2.getId(), pool.getId(), start2, end2);
            System.out.println("✅ Stworzono rezerwację 2: " + client2.getFirstName() + " na " + pool.getName());

            // Rezerwacja 3: Piotr Wiśniewski na Korty Rakieta (POPRAWIONA REZERWACJA)
            Rental rental3 = rentalService.rentFacility(client3.getId(), court.getId(), start3, end3);
            System.out.println("✅ Stworzono rezerwację 3: " + client3.getFirstName() + " na " + court.getName());

        } catch (RentalException e) {
            System.err.println("❌ BŁĄD podczas tworzenia rezerwacji testowych: " + e.getMessage());
        }

        // === 8. ZAMKNIĘCIE POŁĄCZENIA ===
        System.out.println("\n--- Wypełnianie danymi zakończone ---");
        System.out.println("Odśwież MongoDB Compass, aby zobaczyć dane w kolekcjach 'clients', 'facilities' i 'rentals'.");
        MongoDbManager.close();
    }
}