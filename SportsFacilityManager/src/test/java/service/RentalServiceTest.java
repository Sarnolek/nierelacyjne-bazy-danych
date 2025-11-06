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
        db.getCollection("rentals").drop();
        db.getCollection("clients").drop();
        db.getCollection("facilities").drop();

        try {
            Document schema = Document.parse("{" +
                    "  $jsonSchema: {" +
                    "    bsonType: 'object'," +
                    "    properties: {" +
                    "      is_rented: {" +
                    "        bsonType: 'int'," +
                    "        minimum: 0," +
                    "        maximum: 1" +
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
        }

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

        SportsFacility facilityAfter = facilityRepo.findById(testFacility.getId()).get();
        assertEquals(1, facilityAfter.getIsRented(), "Flaga 'is_rented' powinna być 1 po rezerwacji");
    }

    @Test
    @Order(2)
    void testRentFacility_Fail_OverlappingTime() throws RentalException {
        LocalDateTime start1 = LocalDateTime.now().plusDays(1).withHour(10);
        LocalDateTime end1 = start1.plusHours(2);

        rentalService.rentFacility(testClient.getId(), testFacility.getId(), start1, end1);

        LocalDateTime start2 = LocalDateTime.now().plusDays(1).withHour(11);
        LocalDateTime end2 = start2.plusHours(1);

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
            rentalService.rentFacility(UUID.randomUUID(), testFacility.getId(), start, end);
        });

        assertTrue(exception.getMessage().contains("Klient o ID"));
    }

    @Test
    @Order(4)
    void testRentFacility_Fail_SchemaValidation() throws RentalException {
        LocalDateTime start1 = LocalDateTime.now().plusDays(1).withHour(10);
        LocalDateTime end1 = start1.plusHours(2);
        rentalService.rentFacility(testClient.getId(), testFacility.getId(), start1, end1);

        LocalDateTime start2 = LocalDateTime.now().plusDays(2).withHour(10);
        LocalDateTime end2 = start2.plusHours(2);

        RentalException exception = assertThrows(RentalException.class, () -> {
            rentalService.rentFacility(testClient.getId(), testFacility.getId(), start2, end2);
        });

        assertTrue(exception.getMessage().contains("jest juz wypozyczony (Walidacja schematu)"));
    }
}