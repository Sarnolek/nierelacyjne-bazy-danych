package repository;

import model.SportsFacility;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SportsFacilityRepository {
    SportsFacility save(SportsFacility sportsFacility);
    Optional<SportsFacility> findById(UUID id);
    List<SportsFacility> findAll();
    void deleteById(UUID id);
}
