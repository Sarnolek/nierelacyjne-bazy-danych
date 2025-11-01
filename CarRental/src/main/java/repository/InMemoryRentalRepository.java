package repository;

import model.Rental;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryRentalRepository implements RentalRepository{
    private HashMap<UUID, Rental> rentals = new HashMap<>();
    @Override
    public Rental save(Rental rental) {
        rentals.put(rental.getId(), rental);
        return rental;
    }

    @Override
    public Optional<Rental> findById(UUID id) {
        return Optional.ofNullable(rentals.get(id));
    }

    @Override
    public List<Rental> findAll() {
        return new ArrayList<>(rentals.values());
    }

    @Override
    public void deleteById(UUID id) {
        rentals.remove(id);
    }

    @Override
    public List<Rental> findByClientId(UUID clientId) {
        return rentals.values().stream().filter(rental -> rental.getClientId().equals(clientId)).collect(Collectors.toList());
    }

    @Override
    public List<Rental> findByFacilityId(UUID facilityId) {
        return rentals.values().stream()
                .filter(rental -> rental.getFacilityId().equals(facilityId))
                .collect(Collectors.toList());
    }
}
