package service;

import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationOptions;
import db.MongoDbManager;
import model.Client;
import model.Gym;
import model.Rental;
import model.SportsFacility;
import org.bson.Document;
import java.util.UUID;
import org.junit.jupiter.api.*;
import repository.ClientRepository;
import repository.RentalRepository;
import repository.SportsFacilityRepository;
import repository.mongo.ClientMongoRepository;
import repository.mongo.RentalMongoRepository;
import repository.mongo.SportsFacilityMongoRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RentalServiceTest {

    private static RentalService rentalService;
    private static ClientRepository clientRepo;
    private static SportsFacilityRepository facilityRepo;
    private static RentalRepository rentalRepo;
    private static MongoDatabase db;

    private Client testClient;
    private SportsFacility testFacility;

    @BeforeAll
    static void setup() {
        MongoDbManager.init();
        db = MongoDbManager.getDatabase();
        clientRepo = new ClientMongoRepository();
        facilityRepo = new SportsFacilityMongoRepository();
        rentalRepo = new RentalMongoRepository();
        rentalService = new RentalServiceImpl(clientRepo, facilityRepo, rentalRepo);
    }

    @AfterAll
    static void tearDown() {
        MongoDbManager.close();
    }

    @BeforeEach
    void setupEach() {
        // Czyścimy wszystkie kolekcje
        db.getCollection("rentals").drop();
        db.getCollection("clients").drop();
        db.getCollection("facilities").drop();

        // Ponownie tworzymy kolekcję 'facilities' z walidacją schematu (jak w Main.java)
        // To jest kluczowe dla testu logiki biznesowej
        try {
            Document schema = Document.parse("{" +
                    "  $jsonSchema: {" +
                    "    bsonType: 'object'," +
                    "    properties: {" +
                    "      is_rented: {" +
                    "        bsonType: 'int'," +
                    "        minimum: 0," +
                    "        maximum: 1" + // REGULAMIN BIZNESOWY
                    "      }" +
                    "    }" +
                    "  }" +
                    "}");
            ValidationOptions validationOptions = new ValidationOptions()
                    .validator(schema)
                    .validationAction(ValidationAction.ERROR);
            db.createCollection(
                    "facilities",
                    new CreateCollectionOptions().validationOptions(validationOptions)
            );
        } catch (MongoCommandException e) {
            // Kolekcja już istnieje (może się zdarzyć w rzadkich przypadkach)
        }

        // Tworzymy dane testowe
        testClient = new Client("Testowy", "Klient");
        testFacility = new Gym("Testowa Siłownia", 10.0, 10, 100, false);
        clientRepo.save(testClient);
        facilityRepo.save(testFacility);
    }

    @Test
    @Order(1)
    void testRentFacility_Success() throws RentalException {
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10);
        LocalDateTime end = start.plusHours(2);

        Rental rental = rentalService.rentFacility(testClient.getId(), testFacility.getId(), start, end);

        assertNotNull(rental);
        assertEquals(testClient.getId(), rental.getClientId());

        // Weryfikacja, czy flaga 'is_rented' została ustawiona na 1
        SportsFacility facilityAfter = facilityRepo.findById(testFacility.getId()).get();
        assertEquals(1, facilityAfter.getIsRented(), "Flaga 'is_rented' powinna być 1 po rezerwacji");
    }

    @Test
    @Order(2)
    void testRentFacility_Fail_OverlappingTime() throws RentalException {
        LocalDateTime start1 = LocalDateTime.now().plusDays(1).withHour(10);
        LocalDateTime end1 = start1.plusHours(2); // 10:00 - 12:00

        // Pierwsza rezerwacja - powinna się udać
        rentalService.rentFacility(testClient.getId(), testFacility.getId(), start1, end1);

        LocalDateTime start2 = LocalDateTime.now().plusDays(1).withHour(11); // 11:00 - 12:00
        LocalDateTime end2 = start2.plusHours(1);

        // Druga rezerwacja (nakładająca się) - powinna rzucić wyjątkiem (sprawdzenie logiki w serwisie)
        RentalException exception = assertThrows(RentalException.class, () -> {
            rentalService.rentFacility(testClient.getId(), testFacility.getId(), start2, end2);
        });

        assertTrue(exception.getMessage().contains("jest juz wypozyczony w tym terminie"));
    }

    @Test
    @Order(3)
    void testRentFacility_Fail_ClientNotFound() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(2);

        RentalException exception = assertThrows(RentalException.class, () -> {
            // Używamy losowego UUID, który nie istnieje w bazie
            rentalService.rentFacility(UUID.randomUUID(), testFacility.getId(), start, end);
        });

        assertTrue(exception.getMessage().contains("Klient o ID"));
    }

    @Test
    @Order(4)
    void testRentFacility_Fail_SchemaValidation() throws RentalException {
        // Ten test sprawdza regułę biznesową zaimplementowaną na poziomie bazy danych

        // 1. Pierwsza rezerwacja (ustawia is_rented = 1)
        LocalDateTime start1 = LocalDateTime.now().plusDays(1).withHour(10);
        LocalDateTime end1 = start1.plusHours(2);
        rentalService.rentFacility(testClient.getId(), testFacility.getId(), start1, end1);

        // 2. Druga rezerwacja (inny termin, więc przejdzie walidację serwisu,
        //    ale próba inkrementacji 'is_rented' z 1 na 2 zostanie zablokowana przez bazę danych)
        LocalDateTime start2 = LocalDateTime.now().plusDays(2).withHour(10);
        LocalDateTime end2 = start2.plusHours(2);

        // Serwis spróbuje wykonać Updates.inc("is_rented", 1), co da 2
        // Baza danych rzuci MongoWriteException (code 121), bo 'maximum: 1'
        // Serwis powinien to złapać i opakować w RentalException

        RentalException exception = assertThrows(RentalException.class, () -> {
            rentalService.rentFacility(testClient.getId(), testFacility.getId(), start2, end2);
        });

        // Weryfikujemy, czy wyjątek pochodzi z walidacji schematu (zgodnie z logiką w RentalServiceImpl)
        assertTrue(exception.getMessage().contains("jest juz wypozyczony (Walidacja schematu)"));
    }
}