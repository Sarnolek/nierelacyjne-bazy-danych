package service;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import db.KafkaTopicManager;
import db.MongoDbManager;
import db.RedisDbManager;
import model.Client;
import model.Gym;
import model.Rental;
import model.SportsFacility;
import org.bson.Document;
import org.junit.jupiter.api.*;
import repository.ClientRepository;
import repository.RentalRepository;
import repository.SportsFacilityRepository;
import repository.mongo.ClientMongoRepository;
import repository.mongo.RentalMongoRepository;
import repository.mongo.SportsFacilityMongoRepository;
import repository.redis.ClientRepositoryRedisDecorator;
import repository.redis.SportsFacilityRepositoryRedisDecorator;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class KafkaEndToEndTest {

    private static RentalService rentalService;
    private static ClientRepository clientRepo;
    private static SportsFacilityRepository facilityRepo;
    private static MongoDatabase db;

    private static ExecutorService consumerExecutor;

    @BeforeAll
    static void setup() {
        MongoDbManager.init();
        RedisDbManager.init();
        db = MongoDbManager.getDatabase();

        KafkaTopicManager.createTopic();

        clientRepo = new ClientRepositoryRedisDecorator(new ClientMongoRepository());
        facilityRepo = new SportsFacilityRepositoryRedisDecorator(new SportsFacilityMongoRepository());
        RentalRepository rentalRepo = new RentalMongoRepository();
        rentalService = new RentalServiceImpl(clientRepo, facilityRepo, rentalRepo);

        consumerExecutor = Executors.newSingleThreadExecutor();
        consumerExecutor.submit(new RentalAnalyticsConsumer("Test-Consumer"));

        try { Thread.sleep(3000); } catch (InterruptedException e) {}
    }

    @AfterAll
    static void tearDown() {
        if (consumerExecutor != null) {
            consumerExecutor.shutdownNow();
        }
        MongoDbManager.close();
        RedisDbManager.close();
    }

    @BeforeEach
    void clearData() {
        db.getCollection("rentals").drop();
        db.getCollection("rental_analytics").drop();

        Client c = new Client("Test", "User");
        c.setId(UUID.randomUUID());
        clientRepo.save(c);

        SportsFacility f = new Gym("Test Gym", 100.0, 10, 50, true);
        f.setId(UUID.randomUUID());
        f.setIsRented(0);
        facilityRepo.save(f);
    }

    @Test
    @Order(1)
    @DisplayName("Pełny przepływ: Wypożyczenie -> Kafka -> Mongo Analytics")
    void testFullFlow() throws RentalException, InterruptedException {
        Client client = clientRepo.findAll().get(0);
        SportsFacility facility = facilityRepo.findAll().get(0);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);

        System.out.println("Tworzę wypożyczenie...");
        Rental rental = rentalService.rentFacility(client.getId(), facility.getId(), start, end);
        assertNotNull(rental, "Wypożyczenie powinno zostać utworzone");

        boolean messageReceived = false;
        Document analyticsDoc = null;

        for (int i = 0; i < 10; i++) {
            analyticsDoc = db.getCollection("rental_analytics")
                    .find(Filters.eq("_id", rental.getId().toString()))
                    .first();

            if (analyticsDoc != null) {
                messageReceived = true;
                break;
            }
            Thread.sleep(1000);
            System.out.println("Czekam na Kafkę... " + (i+1));
        }

        assertTrue(messageReceived, "Konsument nie zapisał danych w 'rental_analytics' w wyznaczonym czasie!");

        System.out.println("Znaleziono dokument analityczny: " + analyticsDoc.toJson());

        Document rawData = (Document) analyticsDoc.get("raw_data");
        assertNotNull(rawData, "Dokument powinien zawierać pole raw_data");
        assertEquals(facility.getName(), rawData.getString("facilityName"), "Kafka powinna przenieść nazwę obiektu");
        assertEquals("Test-Consumer", analyticsDoc.getString("processed_by"));
    }
}