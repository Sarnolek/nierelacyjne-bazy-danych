package repository.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.Node;
import com.datastax.oss.driver.api.core.metadata.NodeState;
import config.CassandraSessionManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.Collection;

public abstract class BaseCassandraTest {

    protected static CqlSession session;
    protected static SportsFacilityMapper mapper;

    private static boolean schemaInitialized = false;

    @BeforeAll
    static void beforeAll() {
        session = CassandraSessionManager.getSession();

        synchronized (BaseCassandraTest.class) {
            if (!schemaInitialized) {
                waitForClusterStabilization();

                createSchema(session);

                schemaInitialized = true;
            } else {
                session.execute("USE sports_ks");
            }
        }

        mapper = SportsFacilityMapper.builder(session).build();
    }

    private static void waitForClusterStabilization() {
        System.out.println("Oczekiwanie na stabilizację klastra (wymagane 2 węzły UP)...");
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 60000) {
            Collection<Node> nodes = session.getMetadata().getNodes().values();
            long upNodes = nodes.stream()
                    .filter(n -> n.getState() == NodeState.UP)
                    .count();

            if (upNodes >= 2) {
                System.out.println("Klaster stabilny. Widoczne węzły: " + upNodes);
                return;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Klaster nie osiągnął stabilności (nie wykryto 2 węzłów) w czasie 60s.");
    }

    private static void createSchema(CqlSession session) {
        executeDdl(session, "DROP KEYSPACE IF EXISTS sports_ks");

        executeDdl(session, "CREATE KEYSPACE sports_ks WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 2}");

        session.execute("USE sports_ks");

        executeDdl(session, "CREATE TABLE IF NOT EXISTS clients (id uuid PRIMARY KEY, first_name text, last_name text)");

        executeDdl(session, "CREATE TABLE IF NOT EXISTS facilities (" +
                "id uuid PRIMARY KEY, " +
                "name text, " +
                "price_per_hour double, " +
                "capacity int, " +
                "facility_type text, " +
                "surface_type text, " +
                "is_indoor boolean, " +
                "area_in_sqm int, " +
                "has_sauna boolean, " +
                "pool_length int, " +
                "number_of_lanes int" +
                ")");

        executeDdl(session, "CREATE TABLE IF NOT EXISTS rentals_by_client (client_id uuid, start_time timestamp, rental_id uuid, facility_id uuid, end_time timestamp, PRIMARY KEY ((client_id), start_time, rental_id)) WITH CLUSTERING ORDER BY (start_time DESC, rental_id ASC)");

        executeDdl(session, "CREATE TABLE IF NOT EXISTS rentals_by_facility (facility_id uuid, start_time timestamp, rental_id uuid, client_id uuid, end_time timestamp, PRIMARY KEY ((facility_id), start_time, rental_id)) WITH CLUSTERING ORDER BY (start_time DESC, rental_id ASC)");

        executeDdl(session, "CREATE TABLE IF NOT EXISTS rentals_by_id (rental_id uuid PRIMARY KEY, client_id uuid, facility_id uuid, start_time timestamp, end_time timestamp)");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void executeDdl(CqlSession session, String query) {
        session.execute(query);
        int tries = 0;
        while (!session.checkSchemaAgreement() && tries < 30) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            tries++;
        }
    }

    @AfterAll
    static void afterAll() {
    }
}