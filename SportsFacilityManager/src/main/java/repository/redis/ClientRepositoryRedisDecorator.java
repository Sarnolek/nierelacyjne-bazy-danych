package repository.redis;

import db.RedisDbManager;
import model.Client;
import repository.ClientRepository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import util.JsonbManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * DEKORATOR:
 * Ta klasa "opakowuje" inne repozytorium klientów (np. Mongo)
 * i dodaje do niego funkcjonalność cache'owania w Redis.
 * Implementuje ten sam interfejs,
 * dzięki czemu z punktu widzenia reszty aplikacji jest "przezroczysta".
 */
public class ClientRepositoryRedisDecorator implements ClientRepository {

    private final ClientRepository innerRepository; //wewnetrrzne repozytorum(chodzi o mongosa)
    private static final String KEY_PREFIX = "client:";
    //czas zycia cahe
    private static final long CACHE_TTL_SEC = 600;


    /**
     * W konstruktorze "wstrzykujemy" prawdziwe repozytorium,
     * które ten dekorator będzie opakowywał.
     */
    public ClientRepositoryRedisDecorator(ClientRepository innerRepository) {
        this.innerRepository = innerRepository;
    }


    /**
     * Metoda WRITE (Zapis/Aktualizacja) - Logika Inwalidacji Cache'a.
     */
    @Override
    public Client save(Client client) {
        //1. zapisanie danych w mongo
        Client savedClient = innerRepository.save(client);

        //2. unieważnij dane w Cache (Redis)
        String key = KEY_PREFIX +savedClient.getId().toString();
        try(Jedis jedis = RedisDbManager.getJedis()){
            jedis.del(key);
        }catch(JedisException e){
            System.err.println("unieważnienie danych w cache sie nie powiodło(SAVE): " + e.getMessage());
        }
        return savedClient;
    }

    @Override
    public Optional<Client> findById(UUID id) {
        String key = KEY_PREFIX + id.toString();

        //1.próba odczytania danych z Redisa
        try(Jedis jedis = RedisDbManager.getJedis()) {
            String jsonClient = jedis.get(key);

            if(jsonClient != null && !jsonClient.isEmpty()){
                //dane znalezione w Redis
                Client client = JsonbManager.jsonb.fromJson(jsonClient, Client.class);
                return Optional.of(client);
            }

        } catch(JedisException e){
            System.err.println("Nie działa Redis: "+e.getMessage());
        }


        //2. jak nie ma danych w cahe to trzeba oposzukać w mongosie
        Optional<Client> clientFromMongo = innerRepository.findById(id);

        //3. zapisz w cache (jeśli dane istnieją)
        if(clientFromMongo.isPresent()){
            try(Jedis jedis = RedisDbManager.getJedis()){
                //serializacja obiektu z mongo do JSON
                String jsonToCache = JsonbManager.jsonb.toJson(clientFromMongo.get());

                //zapis w redis z czasem wygasnienia (setex = set with expiry)
                jedis.setex(key, CACHE_TTL_SEC, jsonToCache);
            }catch(JedisException e){
                System.err.println("błąd zapisu Redis: "+e.getMessage());
            }
        }
        //zwrot danych pobranych z Mongosa
        return clientFromMongo;
    }

    /**
     * Metoda READ (Odczyt Listy) - Bez Cache'owania.
     */
    @Override
    public List<Client> findAll() {
        //nie cachuje sie całych list wiec przekazujemy to wywołanie do mongosa
        return innerRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
    //1. usuniecie z mongo
        innerRepository.deleteById(id);

    //2. usuniecie z cache
        String key = KEY_PREFIX + id.toString();
        try(Jedis jedis = RedisDbManager.getJedis()){
            jedis.del(key);
        } catch(JedisException e){
            System.err.println("Usuwanie danych z Cache nie powiodło sie (DEL)" + e.getMessage());
        }

    }
}
