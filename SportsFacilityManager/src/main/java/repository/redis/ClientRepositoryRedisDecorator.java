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


public class ClientRepositoryRedisDecorator implements ClientRepository {

    private final ClientRepository innerRepository;
    private static final String KEY_PREFIX = "client:";
    private static final long CACHE_TTL_SEC = 600;



    public ClientRepositoryRedisDecorator(ClientRepository innerRepository) {
        this.innerRepository = innerRepository;
    }



    @Override
    public Client save(Client client) {
        Client savedClient = innerRepository.save(client);

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

        try(Jedis jedis = RedisDbManager.getJedis()) {
            String jsonClient = jedis.get(key);

            if(jsonClient != null && !jsonClient.isEmpty()){
                Client client = JsonbManager.jsonb.fromJson(jsonClient, Client.class);
                return Optional.of(client);
            }

        } catch(JedisException e){
            System.err.println("Nie działa Redis: "+e.getMessage());
        }


        Optional<Client> clientFromMongo = innerRepository.findById(id);

        if(clientFromMongo.isPresent()){
            try(Jedis jedis = RedisDbManager.getJedis()){
                String jsonToCache = JsonbManager.jsonb.toJson(clientFromMongo.get());

                jedis.setex(key, CACHE_TTL_SEC, jsonToCache);
            }catch(JedisException e){
                System.err.println("błąd zapisu Redis: "+e.getMessage());
            }
        }
        return clientFromMongo;
    }


    @Override
    public List<Client> findAll() {

        return innerRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        innerRepository.deleteById(id);

        String key = KEY_PREFIX + id.toString();
        try(Jedis jedis = RedisDbManager.getJedis()){
            jedis.del(key);
        } catch(JedisException e){
            System.err.println("Usuwanie danych z Cache nie powiodło sie (DEL)" + e.getMessage());
        }

    }
}
