package db;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RedisDbManager {


    private static JedisPool jedisPool;


    private static String redisHost;
    private static int redisPort;


    public static void init() {
        if (jedisPool != null) {
            return;
        }

        loadConfig();

        try {
            final JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(10);
            poolConfig.setMaxIdle(5);
            poolConfig.setMinIdle(1);
            poolConfig.setBlockWhenExhausted(true);
            poolConfig.setMaxWaitMillis(5000);


            jedisPool = new JedisPool(poolConfig, redisHost, redisPort);

            try (Jedis jedis = getJedis()) {
                String pingResult = jedis.ping();
                System.out.println("Nawiązano połączenie z Redisem (" + redisHost + ":" + redisPort + "): " + pingResult);
            }

        } catch (JedisException e) {
            System.err.println("Nie udało się połączyć z Redisem: " + e.getMessage());
        }
    }


    private static void loadConfig() {
        Properties prop = new Properties();

        try (InputStream input = RedisDbManager.class.getClassLoader().getResourceAsStream("application.properties")) {

            if (input == null) {
                System.err.println("Nie znaleziono pliku application.properties! Używam wartości domyślnych.");

                redisHost = "localhost";
                redisPort = 6379;
                return;
            }

            prop.load(input);


            redisHost = prop.getProperty("redis.host");
            redisPort = Integer.parseInt(prop.getProperty("redis.port"));

        } catch (IOException | NumberFormatException e) {
            System.err.println("Błąd podczas wczytywania konfiguracji: " + e.getMessage());
            redisHost = "localhost";
            redisPort = 6379;
        }
    }

    public static Jedis getJedis() {
        if (jedisPool == null) {
            System.err.println("Pula nie została zainicjowana! Wywołuję init().");
            init();
        }
        return jedisPool.getResource();
    }

    public static void close() {
        if (jedisPool != null) {
            jedisPool.close();
            jedisPool = null;
            System.out.println("Zamknięto pulę Redis");
        }
    }
}