import db.MongoDbManager;
import db.RedisDbManager;
import model.*;
import repository.ClientRepository;
import repository.RentalRepository;
import repository.SportsFacilityRepository;
import repository.mongo.ClientMongoRepository;
import repository.mongo.RentalMongoRepository;
import repository.mongo.SportsFacilityMongoRepository;
import repository.redis.ClientRepositoryRedisDecorator;
import repository.redis.SportsFacilityRepositoryRedisDecorator;
import service.RentalAnalyticsConsumer;
import service.RentalException;
import service.RentalService;
import service.RentalServiceImpl;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationOptions;
import org.bson.Document;
import com.mongodb.MongoCommandException;

import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        MongoDbManager.init();
        RedisDbManager.init();
        db.KafkaTopicManager.createTopic();

        String mode = (args.length > 0) ? args[0] : "help";

        switch (mode) {
            case "setup":
                runSetup();
                break;
            case "consumer":
                runConsumer();
                break;
            case "producer":
                runProducer();
                break;
            default:
                System.out.println("1. setup    -> Czyści bazę i przygotowuje kolekcje");
                System.out.println("2. consumer -> Uruchamia proces konsumenta (odpal w wielu oknach)");
                System.out.println("3. producer -> Wysyła nowe dane (rezerwacje) do Kafki");
                runSetup();
                new Thread(Main::runConsumer).start();
                try { Thread.sleep(2000); } catch (InterruptedException e) {}
                runProducer();
        }

        if (mode.equals("consumer")) {
            System.out.println("Naciśnij Enter, aby zakończyć konsumenta...");
            new Scanner(System.in).nextLine();
        } else if (!mode.equals("help")) {
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
            MongoDbManager.close();
            RedisDbManager.close();
        }
    }

    private static void runSetup() {
        System.out.println("URUCHAMIANIE SETUPU (Czyszczenie bazy)");
        try {
            MongoDbManager.getDatabase().getCollection("facilities").drop();
            MongoDbManager.getDatabase().getCollection("clients").drop();
            MongoDbManager.getDatabase().getCollection("rentals").drop();
            MongoDbManager.getDatabase().getCollection("rental_analytics").drop();
            System.out.println("Kolekcje wyczyszczone.");
        } catch (Exception e) {
            System.out.println("Brak kolekcji do wyczyszczenia.");
        }

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
            MongoDbManager.getDatabase().createCollection(
                    "facilities",
                    new CreateCollectionOptions().validationOptions(validationOptions)
            );
            System.out.println("Kolekcja 'facilities' stworzona.");
        } catch (MongoCommandException e) {
        }

        ClientRepository clientRepo = new ClientRepositoryRedisDecorator(new ClientMongoRepository());
        SportsFacilityRepository facilityRepo = new SportsFacilityRepositoryRedisDecorator(new SportsFacilityMongoRepository());

        clientRepo.save(new Client("Jan", "Kowalski"));
        clientRepo.save(new Client("Anna", "Nowak"));

        facilityRepo.save(new Gym("Siłownia Gold", 50.0, 20, 300, true));
        facilityRepo.save(new SwimmingPool("Pływalnia Fala", 80.0, 50, 25, 6));
        facilityRepo.save(new TennisCourt("Korty Rakieta", 65.0, 4, SurfaceType.CLAY, false));

        System.out.println(">>> SETUP ZAKOŃCZONY. Baza gotowa.");
    }

    private static void runConsumer() {
        String instanceId = "Konsument-" + UUID.randomUUID().toString().substring(0, 4);
        System.out.println(">>> STARTUJĘ KONSUMENTA: " + instanceId);

        new RentalAnalyticsConsumer(instanceId).run();
    }

    private static void runProducer() {
        System.out.println(">>> STARTUJĘ PRODUCENTA (Tworzenie rezerwacji)...");

        ClientRepository clientRepo = new ClientRepositoryRedisDecorator(new ClientMongoRepository());
        SportsFacilityRepository facilityRepo = new SportsFacilityRepositoryRedisDecorator(new SportsFacilityMongoRepository());
        RentalRepository rentalRepo = new RentalMongoRepository();
        RentalService rentalService = new RentalServiceImpl(clientRepo, facilityRepo, rentalRepo);

        try {
            Client client = clientRepo.findAll().get(0);
            SportsFacility facility = facilityRepo.findAll().stream()
                    .filter(f -> f.getIsRented() == 0)
                    .findFirst()
                    .orElse(facilityRepo.findAll().get(0));

            LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(12).withMinute(0);
            LocalDateTime end = start.plusHours(1);

            System.out.println("Próba wypożyczenia: " + facility.getName() + " dla " + client.getFirstName());
            rentalService.rentFacility(client.getId(), facility.getId(), start, end);

            System.out.println(">>> PRODUCENT: Wysłano rezerwację.");
        } catch (Exception e) {
            System.err.println("Błąd producenta: " + e.getMessage());
        }
    }
}