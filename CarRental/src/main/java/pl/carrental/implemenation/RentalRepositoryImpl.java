package pl.carrental.implemenation;

import jakarta.persistence.EntityManager;
import pl.carrental.repository.RentalRepository;
import pl.carrental.vehicle.Vehicle;
import pl.carrental.client.Client;
import pl.carrental.service.Rental;

import java.util.List;
import java.util.Optional;

public class RentalRepositoryImpl implements RentalRepository {
    private final EntityManager em;

    public RentalRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Rental rental) {
        if (rental.getId() == null) {
            em.persist(rental);
        } else {
            em.merge(rental);
        }
    }

    @Override
    public Optional<Rental> findById(Long id) {
        return Optional.ofNullable(em.find(Rental.class, id));
    }

    @Override
    public List<Rental> findAll() {
        return em.createQuery("SELECT r FROM Rental r", Rental.class)
                .getResultList();
    }

    @Override
    public Optional<Rental> findByRentalId(Long rentalId){
        return em.createQuery("SELECT r FROM Rental r WHERE r.rentalId = :rentalId", Rental.class)
                .setParameter("rentalId", rentalId)
                .getResultStream()
                .findFirst();
    }


    @Override
    public List<Rental> findByClient(Client client) {
        return em.createQuery("SELECT r FROM Rental r WHERE r.client = :client", Rental.class)
                .setParameter("client", client)
                .getResultList();
    }

    @Override
    public List<Rental> findByVehicle(Vehicle vehicle) {
        return em.createQuery("SELECT r FROM Rental r WHERE r.vehicle = :vehicle", Rental.class)
                .setParameter("vehicle", vehicle)
                .getResultList();
    }

    @Override
    public void delete(Rental rental) {
        if (em.contains(rental)) {
            em.remove(rental);
        } else {
            Rental managedRental = em.find(Rental.class, rental.getId());
            if (managedRental != null) {
                em.remove(managedRental);
            }
        }
    }
}