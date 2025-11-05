package repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import db.MongoDbManager;
import model.Client;
import repository.ClientRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientMongoRepository implements ClientRepository {
    private final MongoCollection<Client> collection;

    public ClientMongoRepository() {
        this.collection = MongoDbManager.getDatabase().getCollection("clients", Client.class);
    }

    @Override
    public Client save(Client client) {
        collection.replaceOne(
                Filters.eq("_id", client.getId()),
                client,
                new ReplaceOptions().upsert(true)
        );
        return client;
    }

    @Override
    public Optional<Client> findById(UUID id) {
        Client client = collection.find(Filters.eq("_id", id)).first();
        return Optional.ofNullable(client);
    }

    @Override
    public List<Client> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    @Override
    public void deleteById(UUID id) {
        collection.deleteOne(Filters.eq("_id", id));
    }
}
