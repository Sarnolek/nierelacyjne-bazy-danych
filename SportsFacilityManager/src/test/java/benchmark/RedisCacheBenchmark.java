package benchmark;

import db.MongoDbManager;
import db.RedisDbManager;
import model.Client;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import redis.clients.jedis.Jedis;
import repository.ClientRepository;
import repository.mongo.ClientMongoRepository;
import repository.redis.ClientRepositoryRedisDecorator;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 1, warmups = 1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
public class RedisCacheBenchmark {

    private ClientRepository mongoOnlyRepo;
    private ClientRepository redisDecoratorRepo;

    private UUID testClientId;
    private String redisKey;


    @Setup(Level.Trial)
    public void setup() {
        MongoDbManager.init();
        RedisDbManager.init();

        mongoOnlyRepo = new ClientMongoRepository();
        redisDecoratorRepo = new ClientRepositoryRedisDecorator(mongoOnlyRepo);

        Client client = new Client("Benchmark", "User");
        testClientId = client.getId();
        redisKey = "client:" + testClientId.toString();

        mongoOnlyRepo.save(client);

        System.out.println("--- SETUP ZAKOŃCZONY: Klient ID " + testClientId + " zapisany ---");
    }


    @TearDown(Level.Trial)
    public void tearDown() {
        if (testClientId != null) {
            mongoOnlyRepo.deleteById(testClientId);
        }
        MongoDbManager.close();
        RedisDbManager.close();
    }


    @Benchmark
    public Client test01_MongoOnly() {
        return mongoOnlyRepo.findById(testClientId).orElse(null);
    }


    @Benchmark
    public Client test02_RedisCacheHit() {
        return redisDecoratorRepo.findById(testClientId).orElse(null);
    }


    @Benchmark
    public Client test03_RedisCacheMiss() {
        try (Jedis jedis = RedisDbManager.getJedis()) {
            jedis.del(redisKey);
        }
        return redisDecoratorRepo.findById(testClientId).orElse(null);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(RedisCacheBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}