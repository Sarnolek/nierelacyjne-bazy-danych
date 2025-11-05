package repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import db.MongoDbManager;
import model.Rental;
import repository.RentalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RentalMongoRepository implements RentalRepository {
    private final MongoCollection<Rental> collection;

    public RentalMongoRepository() {
        this.collection = MongoDbManager.getDatabase().getCollection("rentals", Rental.class);
    }

    @Override
    public Rental save(Rental rental) {
        collection.replaceOne(
                Filters.eq("_id", rental.getId()),
                rental,
                new ReplaceOptions().upsert(true)
        );
        return rental;
    }

    @Override
    public Optional<Rental> findById(UUID id) {
        return Optional.ofNullable(collection.find(Filters.eq("_id", id)).first());
    }

    @Override
    public List<Rental> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    @Override
    public void deleteById(UUID id) {
        collection.deleteOne(Filters.eq("_id", id));
    }

    @Override
    public List<Rental> findByClientId(UUID clientId) {
        return collection.find(Filters.eq("client_id", clientId)).into(new ArrayList<>());
    }

    @Override
    public List<Rental> findByFacilityId(UUID facilityId) {
        return collection.find(Filters.eq("facility_id", facilityId)).into(new ArrayList<>());
    }
}
