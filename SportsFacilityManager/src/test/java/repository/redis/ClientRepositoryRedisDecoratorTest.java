package repository.redis;

import db.RedisDbManager;
import model.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import repository.ClientRepository;
import util.JsonbManager;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientRepositoryRedisDecoratorTest {

    @Mock
    private ClientRepository innerRepo;

    @Mock
    private Jedis jedisMock;

    private ClientRepositoryRedisDecorator decorator;

    @BeforeEach
    void setUp() {
        decorator = new ClientRepositoryRedisDecorator(innerRepo);
    }

    @Test
    void testFindById_CacheHit() {
        System.out.println("\n--- TEST 1: Cache HIT (Dane są w Redis) ---");
        UUID id = UUID.randomUUID();
        Client clientInRedis = new Client("Redis", "Hit");
        clientInRedis.setId(id);
        String jsonClient = JsonbManager.jsonb.toJson(clientInRedis);

        try (MockedStatic<RedisDbManager> mockedRedisManager = mockStatic(RedisDbManager.class)) {
            mockedRedisManager.when(RedisDbManager::getJedis).thenReturn(jedisMock);

            when(jedisMock.get("client:" + id)).thenReturn(jsonClient);

            Optional<Client> result = decorator.findById(id);

            assertTrue(result.isPresent());
            assertEquals("Redis", result.get().getFirstName());

            verify(innerRepo, never()).findById(id);
            System.out.println("SUKCES: Pobrano dane z Redis. Mongo nie zostało odpytane.");
        }
    }

    @Test
    void testFindById_CacheMiss() {
        System.out.println("\n--- TEST 2: Cache MISS (Brak w Redis -> Mongo -> Zapis w Redis) ---");

        UUID id = UUID.randomUUID();
        Client clientInMongo = new Client("Mongo", "Db");
        clientInMongo.setId(id);

        try (MockedStatic<RedisDbManager> mockedRedisManager = mockStatic(RedisDbManager.class)) {
            mockedRedisManager.when(RedisDbManager::getJedis).thenReturn(jedisMock);

            when(jedisMock.get("client:" + id)).thenReturn(null);
            when(innerRepo.findById(id)).thenReturn(Optional.of(clientInMongo));

            Optional<Client> result = decorator.findById(id);

            assertTrue(result.isPresent());
            assertEquals("Mongo", result.get().getFirstName());

            verify(innerRepo).findById(id);
            verify(jedisMock).setex(eq("client:" + id), anyLong(), contains("Mongo"));
            System.out.println("SUKCES: Dane pobrane z Mongo i zapisane do cache Redis.");
        }
    }

    @Test
    void testSave_InvalidatesCache() {
        System.out.println("\n--- TEST 3: Zapis (Inwalidacja Cache) ---");
        Client client = new Client("Jan", "Update");
        UUID id = UUID.randomUUID();
        client.setId(id);

        when(innerRepo.save(client)).thenReturn(client);

        try (MockedStatic<RedisDbManager> mockedRedisManager = mockStatic(RedisDbManager.class)) {
            mockedRedisManager.when(RedisDbManager::getJedis).thenReturn(jedisMock);

            decorator.save(client);

            verify(innerRepo).save(client);
            verify(jedisMock).del("client:" + id);
            System.out.println("SUKCES: Dane zapisane w Mongo, klucz usunięty z Redis.");
        }
    }

    @Test
    void testFindById_RedisFailure_FailoverToMongo() {
        System.out.println("\n--- TEST 4: Awaria Redis (Failover) ---");
        UUID id = UUID.randomUUID();
        Client clientInMongo = new Client("Failover", "Client");

        try (MockedStatic<RedisDbManager> mockedRedisManager = mockStatic(RedisDbManager.class)) {
            mockedRedisManager.when(RedisDbManager::getJedis).thenReturn(jedisMock);

            when(jedisMock.get(anyString())).thenThrow(new JedisConnectionException("Connection lost"));
            when(innerRepo.findById(id)).thenReturn(Optional.of(clientInMongo));

            System.out.println("(Poniższy błąd 'Nie działa Redis' jest oczekiwany!)");
            Optional<Client> result = decorator.findById(id);

            assertTrue(result.isPresent());
            assertEquals("Failover", result.get().getFirstName());

            verify(innerRepo).findById(id);
            System.out.println("SUKCES: Mimo błędu Redis, aplikacja pobrała dane z Mongo.");
        }
    }
}