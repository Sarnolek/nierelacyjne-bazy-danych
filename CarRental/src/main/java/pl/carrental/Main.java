package pl.carrental;

import jakarta.persistence.*;
import pl.carrental.service.RentalService;
import pl.carrental.vehicle.Car;
import pl.carrental.client.Client;
import pl.carrental.client.ClientType;
import pl.carrental.service.Rental;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("POSTGRES_RENT_PU");
        EntityManager em = emf.createEntityManager();

        try {
            // KROK 1: Zapisz dane do bazy w osobnej transakcji
            em.getTransaction().begin();

            Client nowyKlient = new Client(
                    101L,
                    "Anna",
                    "Nowak",
                    "anna.nowak@example.com",
                    ClientType.TYPE1,
                    1000.0
            );

            Car nowySamochod = new Car(
                    202L,
                    "WZ1234A",
                    "Toyota",
                    "Corolla",
                    2023,
                    "Srebrny",
                    false,
                    150.0,
                    5
            );

            // Zapisujemy obiekty w bazie danych!
            em.persist(nowyKlient);
            em.persist(nowySamochod);

            em.getTransaction().commit();
            System.out.println("✅ Klient i pojazd zostali zapisani w bazie danych.");


            // KROK 2: Teraz wykonaj operację wypożyczenia
            // Tworzymy nową instancję serwisu, aby działał na świeżych danych
            RentalService rentalService = new RentalService(em);
            // Wywołujemy metodę, która teraz znajdzie zapisane obiekty
            rentalService.rentVehicle(1L, nowyKlient.getClientId(), nowySamochod.getVehicleId(), 7);

        } catch (Exception e) {
            // Jeśli transakcja wciąż jest aktywna z powodu błędu, wycofaj ją
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("❌ Operacja nie powiodła się: " + e.getMessage());
        } finally {
            em.close();
            emf.close();
        }
    }
}