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

    // Możemy zdefiniować stałe dla naszych reguł
    private static final int MINIMAL_RENTAL_DAYS = 1;

    public RentalService(EntityManager em) {
        this.em = em;
        this.clientRepository = new ClientRepositoryImpl(em);
        this.vehicleRepository = new VehicleRepositoryImpl(em);
        this.rentalRepository = new RentalRepositoryImpl(em);
    }

    public Rental rentVehicle(Long rentalId, Long clientId, Long vehicleId, int days) {
        em.getTransaction().begin();
        try {
            // 1. Pobieramy potrzebne dane
            Client client = clientRepository.findByClientId(clientId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta o ID: " + clientId));

            Vehicle vehicle = vehicleRepository.findByVehicleId(vehicleId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu o ID: " + vehicleId));

            // ==========================================================
            // 2. Zastosuj REGUŁY BIZNESOWE
            // ==========================================================

            // --- Istniejące reguły ---
            if (vehicle.isRented()) {
                throw new IllegalStateException("Pojazd jest już wypożyczony!");
            }
            // Zakładam, że w encji Client masz metodę hasSlotForRent()
            // if (!client.hasSlotForRent()) {
            //     throw new IllegalStateException("Klient osiągnął maksymalny limit wypożyczeń!");
            // }

            // --- NOWA REGUŁA: Minimalny czas wypożyczenia ---
            if (days < MINIMAL_RENTAL_DAYS) {
                throw new IllegalArgumentException("Minimalny okres wypożyczenia to " + MINIMAL_RENTAL_DAYS + " dzień/dni.");
            }

            // --- NOWA REGUŁA: Sprawdzenie salda klienta ---
            double totalCost = vehicle.getDailyPrice() * days;
            if (client.getBalance() < totalCost) {
                throw new IllegalStateException(
                        String.format("Niewystarczające środki na koncie. Wymagane: %.2f zł, dostępne: %.2f zł.", totalCost, client.getBalance())
                );
            }

            // --- NOWA REGUŁA: Ograniczenia typu pojazdu dla klienta ---
            if (client.getClientType() == ClientType.TYPE1 && vehicle instanceof Truck) {
                throw new IllegalStateException("Klient typu 'TYPE1' nie może wypożyczać ciężarówek (Truck).");
            }
            // Można tu dodać więcej reguł, np. dla klientów premium, specjalnych pojazdów etc.


            // 3. Wykonaj operacje, jeśli reguły zostały spełnione
            System.out.println("✅ Wszystkie reguły biznesowe zostały spełnione.");

            // Obciążamy konto klienta
            client.setBalance(client.getBalance() - totalCost);

            // Zmieniamy status pojazdu
            vehicle.setRented(true);

            // Tworzymy nowy obiekt wypożyczenia
            Rental newRental = new Rental(rentalId, client, vehicle, days);
            newRental.setRentalPrice(totalCost); // Ustawiamy obliczoną cenę

            // 4. Zapisz zmiany w bazie
            clientRepository.save(client); // Aktualizacja salda klienta
            vehicleRepository.save(vehicle); // Aktualizacja statusu pojazdu
            rentalRepository.save(newRental); // Stworzenie nowego wypożyczenia

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
}