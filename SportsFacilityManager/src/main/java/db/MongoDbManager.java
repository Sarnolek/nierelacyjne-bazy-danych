package db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class MongoDbManager {
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static void init() {
        String connectionUrl = "mongodb://mongodb1:27017,mongodb2:27018,mongodb3:27019/?replicaSet=sportsfacility_set_single";
        MongoCredential credential = MongoCredential.createCredential(
                "admin", "admin", "adminpassword".toCharArray());

        init(connectionUrl, "sportsfacility", credential);
    }


    public static void init(String connectionUrl, String dbName, MongoCredential credential) {
        if (mongoClient != null) {
            close();
        }

        ConnectionString connectionString = new ConnectionString(connectionUrl);

        CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(
                PojoCodecProvider.builder()
                        .automatic(true)
                        .register("model")
                        .build()
        );

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                pojoCodecRegistry
        );

        MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(codecRegistry);

        if (credential != null) {
            settingsBuilder.credential(credential);
        }

        MongoClientSettings settings = settingsBuilder.build();

        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase(dbName);
    }

    public static MongoDatabase getDatabase() {
        if (database == null) {
            System.err.println("MongoDbManager nie został zainicjowany! Wywołuję domyślne init().");
            init();
        }
        return database;
    }

    public static MongoClient getMongoClient() {
        if (mongoClient == null) {
            System.err.println("MongoDbManager nie został zainicjowany! Wywołuję domyślne init().");
            init();
        }
        return mongoClient;
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
        }
    }
}