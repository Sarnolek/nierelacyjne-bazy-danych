package pl.carrental.repository;

import pl.carrental.vehicle.Vehicle;
import pl.carrental.client.Client;
import pl.carrental.service.Rental;

import java.util.*;

public interface RentalRepository {

    void save(Rental rental);
    Optional<Rental> findById(Long id);
    List<Rental> findAll();
    List<Rental> findByClient(Client client);
    List<Rental> findByVehicle(Vehicle vehicle);
    Optional<Rental> findByRentalId(Long rentalId);
    void delete(Rental rental);
}
