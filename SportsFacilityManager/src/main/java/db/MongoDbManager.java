// W pliku: SportsFacilityManager/src/main/java/db/MongoDbManager.java

package db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import model.Gym;
import model.SportsFacility;
import model.SwimmingPool;
import model.TennisCourt;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

// === DODAJ TE DWA IMPORTY ===
import java.util.List;
import static org.bson.codecs.pojo.Conventions.ANNOTATION_CONVENTION;

public class MongoDbManager {
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static void init() {
        ConnectionString connectionString = new ConnectionString(
                "mongodb://admin:adminpassword@localhost:27017,localhost:27018,localhost:27019/?replicaSet=rs0&authSource=admin"
        );

        CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(
                PojoCodecProvider.builder()
                        .automatic(true) // Automatycznie znajduje klasy
                        .register("model") // Skanuje cały pakiet 'model'
                        .build()
        );
        // ============================================

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                pojoCodecRegistry
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(codecRegistry)
                .build();

        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("sports_facility_db");
    }

    public static MongoDatabase getDatabase() {
        if (database == null) {
            init();
        }
        return database;
    }

    public static MongoClient getMongoClient() {
        if (mongoClient == null) {
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