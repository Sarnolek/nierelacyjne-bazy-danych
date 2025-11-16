package db;

import com.mongodb.client.MongoClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

public class RedisDbManager {

    //singleton przechowujący pulę połączeń
    private static JedisPool jedisPool;

    //Stałe konfiguracyjne - bierzemy je z docker-compose.yml
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;

    //inicjalizacja puli połączeń
    //te metode nrależy raz wykonań przy starcie aplikacji
    public static void init(){
        if(jedisPool != null){
            return;
        }
        try {
            //kofiguracja polaczen
            final JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(10); //max liczba polonczen w puli
            poolConfig.setMaxIdle(5); //max bezczynnych polaczen które musi czekac
            poolConfig.setMinIdle(1); //min bezczynnych polaczen
            poolConfig.setBlockWhenExhausted(true); //czekaj jazeli pula jest pusta
            poolConfig.setMaxWaitMillis(5000); //czekaj max 5sek

            //nowa pulka
            jedisPool = new JedisPool(poolConfig, REDIS_HOST, REDIS_PORT);

            try (Jedis jedis = getJedis()){
                String pingResult = jedis.ping();
                System.out.println("Nawiazano połaczenie z Redisem: " + pingResult);
            }

        }catch (JedisException e){
            System.err.println("Nie udało sie połaczyc z Redistem: " + e.getMessage());
        }
    }

    /**
     * Pobiera pojedyncze połączenie (zasób) z puli.
     *
     * WAŻNE: Tę metodę ZAWSZE należy używać w bloku try-with-resources,
     * aby połączenie automatycznie wróciło do puli po użyciu.
     *
     * Przykład użycia:
     * try (Jedis jedis = RedisDbManager.getJedis()) {
     * jedis.set("klucz", "wartosc");
     * }
     *
     * @return instancja Jedis
     * @throws JedisException jeśli pula nie została zainicjowana
     */
    public static Jedis getJedis(){
        if (jedisPool == null){
            System.err.println("Pulka jescze cosik nie dizała, trzeba zrobić init");
            init();
        }
        return jedisPool.getResource();
    }

    /**
     * Zamyka pulę połączeń.
     * Tę metodę należy wywołać przy zamykaniu aplikacji (np. w Main.java).
     */

    public static void close(){
        if (jedisPool != null){
            jedisPool.close();
            jedisPool = null;
            System.out.println("zamknieto pulke Redis");
        }
    }
}
