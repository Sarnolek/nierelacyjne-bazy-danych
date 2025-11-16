package repository.redis;

import db.RedisDbManager;
import model.SportsFacility;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import repository.ClientRepository;
import repository.SportsFacilityRepository;
import util.JsonbManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * DEKORATOR:
 * Ta klasa "opakowuje" repozytorium SportsFacility (np. Mongo)
 * i dodaje do niego funkcjonalność cache'owania w Redis.
 * Implementuje ten sam interfejs,
 * dzięki czemu z punktu widzenia reszty aplikacji jest "przezroczysta".
 */
public class SportsFacilityRepositoryRedisDecorator implements SportsFacilityRepository {

    private final SportsFacilityRepository innerRepository; //wewnetrrzne repozytorum(chodzi o mongosa)
    private static final String KEY_PREFIX = "facility:";
    //czas zycia cahe
    private static final long CACHE_TTL_SEC = 600;

    public SportsFacilityRepositoryRedisDecorator(SportsFacilityRepository innerRepository) {
        this.innerRepository = innerRepository;
    }



    @Override
    public SportsFacility save(SportsFacility sportsFacility) {
        // --- KROK 1: Zapisz w "źródle prawdy" (Mongo) ---
        SportsFacility savedFacility = innerRepository.save(sportsFacility);

        // --- KROK 2: Unieważnij (usuń) dane w Cache (Redis) ---
        String key = KEY_PREFIX + savedFacility.getId().toString();
        try (Jedis jedis = RedisDbManager.getJedis()) {
            jedis.del(key); // Usuwamy klucz
        } catch (JedisException e) {
            System.err.println("Błąd unieważniania cache'a w Redis (DEL facility): " + e.getMessage());
        }

        return savedFacility;
    }


    /**
     * Metoda READ (Odczyt) - Główna logika Cache-Aside.
     */
    @Override
    public Optional<SportsFacility> findById(UUID id) {
        String key = KEY_PREFIX + id.toString();

        try (Jedis jedis = RedisDbManager.getJedis()) {
            String jsonFacility = jedis.get(key);

            if (jsonFacility != null && !jsonFacility.isEmpty()) {
                SportsFacility facility = JsonbManager.jsonb.fromJson(jsonFacility, SportsFacility.class);
                return Optional.of(facility);
            }
        } catch (JedisException e) {
            System.err.println("Błąd odczytu z Redis (GET facility): " + e.getMessage());
        }

        Optional<SportsFacility> facilityFromMongo = innerRepository.findById(id);

        if (facilityFromMongo.isPresent()) {
            try (Jedis jedis = RedisDbManager.getJedis()) {
                String jsonToCache = JsonbManager.jsonb.toJson(facilityFromMongo.get());


                jedis.setex(key, CACHE_TTL_SEC, jsonToCache);
            } catch (JedisException e) {
                System.err.println("Błąd zapisu do Redis (SETEX facility): " + e.getMessage());
            }
        }

        // Zwracamy dane, które pobraliśmy z Mongo
        return facilityFromMongo;
    }

    @Override
    public List<SportsFacility> findAll() {
        return innerRepository.findAll();
    }


    @Override
    public void deleteById(UUID id) {
        innerRepository.deleteById(id);

        String key = KEY_PREFIX + id.toString();
        try (Jedis jedis = RedisDbManager.getJedis()) {
            jedis.del(key);
        } catch (JedisException e) {
            System.err.println("Błąd unieważniania cache'a w Redis (DEL facility): " + e.getMessage());
        }
    }

}
