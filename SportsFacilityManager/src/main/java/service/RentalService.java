package service;

import model.Rental;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RentalService {
    Rental rentFacility(UUID clientId, UUID facilityId, LocalDateTime startTime, LocalDateTime endTime)
            throws RentalException;
    boolean isFacilityAvailable(UUID facilityId, LocalDateTime startTime, LocalDateTime endTime);
    List<Rental> getRentalsForClient(UUID clientId);
    List<Rental> getRentalsForFacility(UUID facilityId);
}
