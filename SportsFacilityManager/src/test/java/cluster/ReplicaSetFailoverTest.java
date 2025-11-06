package cluster;

import com.mongodb.client.MongoDatabase;
import db.MongoDbManager;
import model.Client;
import org.bson.Document;
import org.junit.jupiter.api.*;
import repository.mongo.ClientMongoRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("failover")
class ReplicaSetFailoverTest {

    private static ClientMongoRepository clientRepo;
    private static MongoDatabase db;
    private static String dockerComposeDir = new File("docker").getAbsolutePath();
    private static String primaryBeforeStop = "";

    @BeforeAll
    static void setup() throws Exception {
        System.out.println("Uruchamianie testu Failover...");

//        System.out.println("Restartowanie klastra docker-compose dla czystego testu...");
//        runDockerCommand("docker-compose down --volumes", false);
//        runDockerCommand("docker-compose up -d", true);
//        System.out.println("Oczekiwanie 45 sekund na stabilizację klastra...");
//        TimeUnit.SECONDS.sleep(45);

        MongoDbManager.init();
        db = MongoDbManager.getDatabase();
        clientRepo = new ClientMongoRepository();
        db.getCollection("clients").drop();
    }

    @AfterAll
    static void tearDown() {
        MongoDbManager.close();
        System.out.println("Przywracanie zatrzymanego kontenera...");
        runDockerCommand("docker-compose start " + primaryBeforeStop, false);
    }

    @Test
    @Order(1)
    void test_01_WriteBeforeFailoverAndIdentifyPrimary() {
        System.out.println("--- KROK 1: Zapis przed awarią i identyfikacja Primary ---");

        Client client1 = new Client("Przed", "Awarią");
        clientRepo.save(client1);

        Document result = db.runCommand(new Document("isMaster", 1));
        primaryBeforeStop = getContainerName(result.getString("primary"));

        assertNotNull(primaryBeforeStop, "Nie udało się zidentyfikować węzła primary");
        System.out.println("Aktualny węzeł primary to: " + primaryBeforeStop);
    }

    @Test
    @Order(2)
    void test_02_StopPrimaryNode() {
        System.out.println("--- KROK 2: Zatrzymywanie węzła primary: " + primaryBeforeStop + " ---");

        assertFalse(primaryBeforeStop.isEmpty(), "Nie zidentyfikowano primary; test przerywany.");
        runDockerCommand("docker-compose stop " + primaryBeforeStop, true);

        System.out.println("Węzeł zatrzymany. Oczekiwanie 10 sekund na wybór nowego primary...");
    }

    @Test
    @Order(3)
    void test_03_WriteAfterFailover() throws InterruptedException {
        System.out.println("--- KROK 3: Próba zapisu po awarii (oczekiwanie na failover) ---");

        Client client2 = new Client("Po", "Awarii");
        boolean writeSuccess = false;

        for (int i = 0; i < 10; i++) {
            try {
                clientRepo.save(client2);
                writeSuccess = true;
                System.out.println("Zapis po awarii udany!");
                break;
            } catch (Exception e) {
                System.out.println("Próba zapisu " + (i+1) + " nieudana (oczekiwanie na primary): " + e.getMessage());
                TimeUnit.SECONDS.sleep(3);
            }
        }

        assertTrue(writeSuccess, "Zapis do bazy danych nie powiódł się po 30 sekundach od awarii primary.");
    }

    @Test
    @Order(4)
    void test_04_VerifyDataConsistency() {
        System.out.println("--- KROK 4: Weryfikacja spójności danych po awarii ---");

        List<Client> clients = clientRepo.findAll();

        assertEquals(2, clients.size(), "Liczba klientów w bazie się nie zgadza.");

        assertTrue(clients.stream().anyMatch(c -> c.getFirstName().equals("Przed")), "Brak danych zapisanych PRZED awarią.");
        assertTrue(clients.stream().anyMatch(c -> c.getFirstName().equals("Po")), "Brak danych zapisanych PO awarii.");

        System.out.println("Test spójności zdany. Dane przed i po awarii są obecne.");
    }


    private static String getContainerName(String mongoHostPort) {
        if (mongoHostPort == null) return null;
        if (mongoHostPort.startsWith("mongodb1")) return "mongo_1";
        if (mongoHostPort.startsWith("mongodb2")) return "mongo_2";
        if (mongoHostPort.startsWith("mongodb3")) return "mongo_3";
        return null;
    }

    private static void runDockerCommand(String command, boolean waitFor) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command.split(" "));
            pb.directory(new File(dockerComposeDir));
            Process p = pb.start();

            if (waitFor) {
                new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println("DOCKER-CMD: " + line);
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                }).start();
                p.waitFor(30, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            System.err.println("Nie udało się wykonać polecenia docker: " + command);
            e.printStackTrace();
            fail("Błąd przy wykonywaniu polecenia docker: " + e.getMessage());
        }
    }
}