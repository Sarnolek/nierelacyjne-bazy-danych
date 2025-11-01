package repository;

import model.Rental;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentalRepository {
    Rental save(Rental rental);
    Optional<Rental> findById(UUID id);
    List<Rental> findAll();
    void deleteById(UUID id);

    List<Rental> findByClientId(UUID clientId);
    List<Rental> findByFacilityId(UUID facilityId);
}
