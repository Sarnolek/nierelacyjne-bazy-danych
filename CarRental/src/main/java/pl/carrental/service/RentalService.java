package pl.carrental.service;

import jakarta.persistence.EntityManager;
import pl.carrental.client.*;
import pl.carrental.implemenation.*;
import pl.carrental.repository.*;
import pl.carrental.vehicle.*;

public class RentalService {
    private final ClientRepository clientRepository;
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final EntityManager em;

    private static final int MINIMAL_RENTAL_DAYS = 1;

    public RentalService(EntityManager em) {
        this.em = em;
        this.clientRepository = new ClientRepositoryImpl(em);
        this.vehicleRepository = new VehicleRepositoryImpl(em);
        this.rentalRepository = new RentalRepositoryImpl(em);
    }
        public Rental rentVehicle(Long clientId, Long vehicleId, int days) {
        em.getTransaction().begin();
        try {
            Client client = clientRepository.findByClientId(clientId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta o ID: " + clientId));

            Vehicle vehicle = vehicleRepository.findByVehicleId(vehicleId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu o ID: " + vehicleId));
            if (vehicle.isRented()) {
                throw new IllegalStateException("Pojazd jest już wypożyczony!");
            }

            if (days < MINIMAL_RENTAL_DAYS) {
                throw new IllegalArgumentException("Minimalny okres wypożyczenia to " + MINIMAL_RENTAL_DAYS + " dzień/dni.");
            }

            double totalCost = vehicle.getDailyPrice() * days;
            if (client.getBalance() < totalCost) {
                throw new IllegalStateException(
                        String.format("Niewystarczające środki na koncie. Wymagane: %.2f zł, dostępne: %.2f zł.", totalCost, client.getBalance())
                );
            }

            System.out.println("Wszystkie reguły biznesowe zostały spełnione.");

            client.setBalance(client.getBalance() - totalCost);

            vehicle.setRented(true);

            Long newRentalId = System.currentTimeMillis();
            Rental newRental = new Rental(newRentalId, client, vehicle, days);
            newRental.setRentalPrice(totalCost);

            clientRepository.save(client);
            vehicleRepository.save(vehicle);
            rentalRepository.save(newRental);

            em.getTransaction().commit();
            System.out.printf("Pomyślnie wypożyczono pojazd! Koszt: %.2f zł.%n", totalCost);
            return newRental;

        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas wypożyczania: " + e.getMessage());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }

    }
    public void returnVehicle(Long rentalId) {
        em.getTransaction().begin();
        try {
            Rental rental = rentalRepository.findByRentalId(rentalId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono wypożyczenia o ID: " + rentalId));

            if (!rental.isActive()) {
                throw new IllegalStateException("To wypożyczenie zostało już zakończone.");
            }

            Vehicle vehicle = rental.getVehicle();
            vehicle.setRented(false);
            rental.finishRent();

            vehicleRepository.save(vehicle);
            rentalRepository.save(rental);

            em.getTransaction().commit();
            System.out.println("Pomyślnie zwrócono pojazd w ramach wypożyczenia ID: " + rentalId);
        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas zwracania pojazdu: " + e.getMessage());
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

}