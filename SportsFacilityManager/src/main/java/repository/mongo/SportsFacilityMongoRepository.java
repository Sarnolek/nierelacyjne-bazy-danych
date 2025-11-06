package repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import db.MongoDbManager;
import model.SportsFacility;
import repository.SportsFacilityRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SportsFacilityMongoRepository implements SportsFacilityRepository {
    private final MongoCollection<SportsFacility> collection;

    public SportsFacilityMongoRepository() {
        this.collection = MongoDbManager.getDatabase().getCollection("facilities", SportsFacility.class);
    }

    @Override
    public SportsFacility save(SportsFacility sportsFacility) {
        collection.replaceOne(
                Filters.eq("_id", sportsFacility.getId()),
                sportsFacility,
                new ReplaceOptions().upsert(true)
        );
        return sportsFacility;
    }

    @Override
    public Optional<SportsFacility> findById(UUID id) {
        SportsFacility facility = collection.find(Filters.eq("_id", id)).first();
        return Optional.ofNullable(facility);
    }

    @Override
    public List<SportsFacility> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    @Override
    public void deleteById(UUID id) {
        collection.deleteOne(Filters.eq("_id", id));
    }
}
