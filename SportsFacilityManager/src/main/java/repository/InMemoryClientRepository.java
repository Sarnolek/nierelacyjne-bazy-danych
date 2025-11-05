package repository;

import model.Client;

import java.util.*;

public class InMemoryClientRepository implements ClientRepository{
    private final HashMap<UUID, Client> clients = new HashMap<>();
    // pod PAS rozważyć ConcurrentHashMap do obłsugi wielowątkowości

    @Override
    public Client save(Client client) {
        clients.put(client.getId(), client);
        return client;
    }

    @Override
    public Optional<Client> findById(UUID id) {
        return Optional.ofNullable(clients.get(id));
    }

    @Override
    public List<Client> findAll() {
        return new ArrayList<>(clients.values());
    }

    @Override
    public void deleteById(UUID id) {
        clients.remove(id);
    }
}
