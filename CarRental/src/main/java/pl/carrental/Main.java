//    package pl.carrental;
//
//    import jakarta.persistence.*;
//    import pl.carrental.repository.ClientRepository;
//    import pl.carrental.service.Rental;
//    import pl.carrental.service.RentalService;
//    import pl.carrental.vehicle.Bicycle;
//    import pl.carrental.vehicle.Car;
//    import pl.carrental.client.Client;
//    import pl.carrental.client.ClientType;
//    import pl.carrental.vehicle.Truck;
//
//    public class Main {
//
//        public static void main(String[] args) {
//            EntityManagerFactory emf = Persistence.createEntityManagerFactory("POSTGRES_RENT_PU");
//            EntityManager em = emf.createEntityManager();
//            RentalService rentalService = new RentalService(em);
//            Rental rental = null;
//
//            try {
//
//               em.getTransaction().begin();
//
//                Client nowyKlient = new Client(
//                        101L,
//                        "Anna",
//                        "Nowak",
//                        "anna.nowak@example.com",
//                        ClientType.STANDARD,
//                        1050.0
//                );
//
//                Client nowyKlient2 = new Client(
//                        90L,
//                        "Krzysztof",
//                        "Gonciarz",
//                        "krzysieg@example.com",
//                        ClientType.ADVANCED,
//                        9999.0
//                );
//
//                Client nowyKlient3 = new Client(
//                        11L,
//                        "Katarzyna",
//                        "Mecinski",
//                        "kaska@example.com",
//                        ClientType.BUSINESS,
//                        9999.0
//                );
//
//                Car nowySamochod = new Car(
//                        202L,
//                        "WZ1234A",
//                        "Toyota",
//                        "Corolla",
//                        2023,
//                        "Srebrny",
//                        150.0,
//                        5
//                );
//                Truck nowySamochod2 = new Truck(
//                        69L,
//                        "EZG666",
//                        "Opla",
//                        "Corsa",
//                        2004,
//                        "Morski",
//                        50.0,
//                        5000.0
//                );
//                Bicycle nowySamochod3 = new Bicycle(
//                        50L,
//                        "ELO1312321",
//                        "xd",
//                        "xd222",
//                        2025,
//                        "Srebrny",
//                        10.0,
//                        "Mountain"
//                );
//
//
//                em.persist(nowyKlient);
//                em.persist(nowyKlient2);
//                em.persist(nowyKlient3);
//                em.persist(nowySamochod);
//                em.persist(nowySamochod2);
//                em.persist(nowySamochod3);
//
//               em.getTransaction().commit();
//                System.out.println(" Klient i pojazd zostali zapisani w bazie danych.");
//
//
//                rentalService.rentVehicle(nowyKlient.getClientId(), nowySamochod.getVehicleId(), 7);
//                rentalService.rentVehicle(nowyKlient2.getClientId(), nowySamochod2.getVehicleId(), 2);
//                rental = rentalService.rentVehicle(nowyKlient3.getClientId(), nowySamochod3.getVehicleId(), 3);
//
//                rentalService.returnVehicle(rental.getRentalId());
//
//            } catch (Exception e) {
//                if (em.getTransaction().isActive()) {
//                    em.getTransaction().rollback();
//                }
//                System.err.println("Operacja nie powiodła się: " + e.getMessage());
//            } finally {
//                em.close();
//                emf.close();
//            }
//        }
//    }






package pl.carrental;

import jakarta.persistence.*;
import pl.carrental.client.Client;
import pl.carrental.client.ClientType;
import pl.carrental.implemenation.ClientRepositoryImpl;
import pl.carrental.implemenation.VehicleRepositoryImpl;
import pl.carrental.repository.ClientRepository;
import pl.carrental.repository.VehicleRepository;
import pl.carrental.service.RentalService;
import pl.carrental.vehicle.Bicycle;
import pl.carrental.vehicle.Car;
import pl.carrental.vehicle.Truck;
import pl.carrental.vehicle.Vehicle;


public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("POSTGRES_RENT_PU");
        EntityManager em = emf.createEntityManager();
        RentalService rentalService = new RentalService(em);

        Long klientDoUsunieciaId = 90L;
        Long pojazdDoUsunieciaId = 202L;

        try {
            System.out.println("--- KROK 1: Tworzenie klientów, pojazdów i wypożyczeń ---");
            em.getTransaction().begin();

            Client nowyKlient = new Client(101L, "Anna", "Nowak", "anna.nowak@example.com", ClientType.STANDARD, 1050.0);
            Client klientDoUsuniecia = new Client(klientDoUsunieciaId, "Krzysztof", "Gonciarz", "krzysieg@example.com", ClientType.ADVANCED, 9999.0);
            Client nowyKlient3 = new Client(11L, "Katarzyna", "Mecinski", "kaska@example.com", ClientType.BUSINESS, 9999.0);

            Vehicle pojazdDoUsuniecia = new Car(pojazdDoUsunieciaId, "WZ1234A", "Toyota", "Corolla", 2023, "Srebrny", 150.0, 5);
            Truck nowySamochod2 = new Truck(69L, "EZG666", "Opel", "Corsa", 2004, "Morski", 50.0, 5000.0);
            Bicycle nowySamochod3 = new Bicycle(50L, "ELO1312321", "xd", "xd222", 2025, "Srebrny", 10.0, "Mountain");

            em.persist(nowyKlient);
            em.persist(klientDoUsuniecia);
            em.persist(nowyKlient3);
            em.persist(pojazdDoUsuniecia);
            em.persist(nowySamochod2);
            em.persist(nowySamochod3);

            em.getTransaction().commit();
            System.out.println("Klient i pojazd zostali zapisani w bazie danych.");

            rentalService.rentVehicle(pojazdDoUsuniecia.getVehicleId(), klientDoUsuniecia.getClientId(), 7); // Klient i pojazd, które usuniemy
            rentalService.rentVehicle(nowySamochod2.getVehicleId(), nowyKlient.getClientId(), 2);

        } catch (Exception e) {
            System.err.println("Operacja inicjalizacji nie powiodła się: " + e.getMessage());
            e.printStackTrace();
            if (em.getTransaction() != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }

        System.out.println("\n--- KROK 2: Demonstracja usuwania klienta i jego konsekwencji ---");
        EntityManager em2 = emf.createEntityManager();
        try {
            ClientRepository clientRepository = new ClientRepositoryImpl(em2);
            em2.getTransaction().begin();

            System.out.println("Wyszukiwanie klienta do usunięcia (clientId: " + klientDoUsunieciaId + ")...");
            Client client = clientRepository.findByClientId(klientDoUsunieciaId)
                    .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono klienta do usunięcia."));

            System.out.println("Próba usunięcia klienta: " + client.getFirstName() + " " + client.getLastName());
            System.out.println(" Ten klient ma powiązane wypożyczenia. Zobaczmy co zrobi JPA...");

            clientRepository.delete(client);

            em2.getTransaction().commit();
            System.out.println("SUKCES! Klient został usunięty.");
            System.out.println("   Dzięki 'CascadeType.ALL', wszystkie wypożyczenia powiązane z tym klientem również zostały usunięte z tabeli 'rentals'.");

        } catch (Exception e) {
            System.err.println("Operacja usuwania klienta nie powiodła się: " + e.getMessage());
            e.printStackTrace();
            if (em2.getTransaction() != null && em2.getTransaction().isActive()) {
                em2.getTransaction().rollback();
            }
        } finally {
            if (em2.isOpen()) {
                em2.close();
            }
        }


        System.out.println("\n--- KROK 3: Demonstracja usuwania pojazdu i jego konsekwencji ---");
        EntityManager em3 = emf.createEntityManager();
        try {
            VehicleRepository vehicleRepository = new VehicleRepositoryImpl(em3);
            em3.getTransaction().begin();

            System.out.println("Wyszukiwanie pojazdu do usunięcia (vehicleId: " + pojazdDoUsunieciaId + ")...");
            Vehicle vehicle = vehicleRepository.findByVehicleId(pojazdDoUsunieciaId)
                    .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono pojazdu do usunięcia."));

            System.out.println("Próba usunięcia pojazdu: " + vehicle.getMake() + " " + vehicle.getModel());
            System.out.println("   Ten pojazd jest powiązany z wypożyczeniem. Zobaczmy co zrobi JPA...");

            vehicleRepository.delete(vehicle);

            em3.getTransaction().commit();
            System.out.println("SUKCES! Pojazd został usunięty.");
            System.out.println("   Podobnie jak przy kliencie, 'CascadeType.ALL' spowodowało usunięcie powiązanych wypożyczeń.");

        } catch (Exception e) {
            System.err.println("Operacja usuwania pojazdu nie powiodła się: " + e.getMessage());
            e.printStackTrace();
            if (em3.getTransaction() != null && em3.getTransaction().isActive()) {
                em3.getTransaction().rollback();
            }
        } finally {
            if (em3.isOpen()) {
                em3.close();
            }
            emf.close(); // Zamykamy na samym końcu
        }
    }
}