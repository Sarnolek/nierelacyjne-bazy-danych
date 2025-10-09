package pl.carrental.repository;

import pl.carrental.vehicle.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository {
    void save(Vehicle vehicle);
    Optional<Vehicle> findById(Long id);
    Optional<Vehicle> findByVehicleId(Long vehicleId);
    List<Vehicle> findAll();
    Optional<Vehicle> findByPlateNumber(String plateNumber);
    void delete(Vehicle vehicle);
}
