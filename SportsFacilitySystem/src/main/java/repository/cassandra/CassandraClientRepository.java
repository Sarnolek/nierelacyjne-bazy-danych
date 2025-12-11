package repository.cassandra;

import model.Client;
import model.cassandra.ClientEntity;
import repository.ClientRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class CassandraClientRepository implements ClientRepository {

    private final ClientDao clientDao;

    public CassandraClientRepository(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    @Override
    public Client save(Client client) {
        ClientEntity entity = new ClientEntity(
                client.getId(),
                client.getFirstName(),
                client.getLastName()
        );

        clientDao.save(entity);

        return client;
    }

    @Override
    public Optional<Client> findById(UUID id) {
        ClientEntity entity = clientDao.findById(id);

        if (entity == null) {
            return Optional.empty();
        }
        Client client = new Client(entity.getFirstName(), entity.getLastName());
        client.setId(entity.getId());
        return Optional.of(client);
    }

    @Override
    public void deleteById(UUID id) {
        ClientEntity entity = new ClientEntity();
        entity.setId(id);
        clientDao.delete(entity);
    }

    @Override
    public List<Client> findAll() {
        throw new UnsupportedOperationException("W Cassandrze unika się pobierania wszystkich rekordów!");
    }
}