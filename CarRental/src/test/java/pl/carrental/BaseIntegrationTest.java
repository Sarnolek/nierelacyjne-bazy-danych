package pl.carrental;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

@Testcontainers
public abstract class BaseIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15-alpine");

    protected static EntityManagerFactory emf;
    protected EntityManager em;

    @BeforeAll
    static void setup() {
        postgresContainer.start();

        Map<String, String> properties = Map.of(
                "jakarta.persistence.jdbc.url", postgresContainer.getJdbcUrl(),
                "jakarta.persistence.jdbc.user", postgresContainer.getUsername(),
                "jakarta.persistence.jdbc.password", postgresContainer.getPassword(),
                "jakarta.persistence.schema-generation.database.action", "drop-and-create"
        );

        emf = Persistence.createEntityManagerFactory("POSTGRES_RENT_PU", properties);
    }

    @AfterAll
    static void tearDown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}