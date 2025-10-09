package pl.carrental.implemenation;

import jakarta.persistence.EntityManager;
import pl.carrental.client.Client;
import pl.carrental.repository.ClientRepository;

import java.util.List;
import java.util.Optional;

public class ClientRepositoryImpl implements ClientRepository {
    private final EntityManager em;

    public ClientRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Client client){
        if (client.getId() == null) {
            em.persist(client);
        } else
            em.merge(client);

    }

    @Override
    public Optional<Client> findById(Long id){
        return Optional.ofNullable(em.find(Client.class, id));
    }

    @Override
    public Optional<Client> findByClientId(Long clientId){
        return em.createQuery("SELECT c FROM Client c WHERE c.clientId = :clientId", Client.class)
                .setParameter("clientId", clientId)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<Client> findAll(){
        return em.createQuery("SELECT c FROM Client c", Client.class)
                .getResultList();
    }

    @Override
    public void delete(Client client){
        if (em.contains(client))
            em.remove(client);
        else {
            Client managedClient = em.find(Client.class, client.getId());
            if (managedClient != null){
                em.remove(managedClient);
            }
        }
    }
}
