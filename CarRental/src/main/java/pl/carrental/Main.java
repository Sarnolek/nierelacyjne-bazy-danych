    package pl.carrental;

    import jakarta.persistence.*;
    import pl.carrental.repository.ClientRepository;
    import pl.carrental.service.Rental;
    import pl.carrental.service.RentalService;
    import pl.carrental.vehicle.Bicycle;
    import pl.carrental.vehicle.Car;
    import pl.carrental.client.Client;
    import pl.carrental.client.ClientType;
    import pl.carrental.vehicle.Truck;

    public class Main {

        public static void main(String[] args) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("POSTGRES_RENT_PU");
            EntityManager em = emf.createEntityManager();
            RentalService rentalService = new RentalService(em);
            Rental rental = null;

            try {

               em.getTransaction().begin();

                Client nowyKlient = new Client(
                        101L,
                        "Anna",
                        "Nowak",
                        "anna.nowak@example.com",
                        ClientType.STANDARD,
                        1050.0
                );

                Client nowyKlient2 = new Client(
                        90L,
                        "Krzysztof",
                        "Gonciarz",
                        "krzysieg@example.com",
                        ClientType.ADVANCED,
                        9999.0
                );

                Client nowyKlient3 = new Client(
                        11L,
                        "Katarzyna",
                        "Mecinski",
                        "kaska@example.com",
                        ClientType.BUSINESS,
                        9999.0
                );

                Car nowySamochod = new Car(
                        202L,
                        "WZ1234A",
                        "Toyota",
                        "Corolla",
                        2023,
                        "Srebrny",
                        150.0,
                        5
                );
                Truck nowySamochod2 = new Truck(
                        69L,
                        "EZG666",
                        "Opla",
                        "Corsa",
                        2004,
                        "Morski",
                        50.0,
                        5000.0
                );
                Bicycle nowySamochod3 = new Bicycle(
                        50L,
                        "ELO1312321",
                        "xd",
                        "xd222",
                        2025,
                        "Srebrny",
                        10.0,
                        "Mountain"
                );


                em.persist(nowyKlient);
                em.persist(nowyKlient2);
                em.persist(nowyKlient3);
                em.persist(nowySamochod);
                em.persist(nowySamochod2);
                em.persist(nowySamochod3);

               em.getTransaction().commit();
                System.out.println(" Klient i pojazd zostali zapisani w bazie danych.");


                rentalService.rentVehicle(nowyKlient.getClientId(), nowySamochod.getVehicleId(), 7);
                rentalService.rentVehicle(nowyKlient2.getClientId(), nowySamochod2.getVehicleId(), 2);
                rental = rentalService.rentVehicle(nowyKlient3.getClientId(), nowySamochod3.getVehicleId(), 3);

                rentalService.returnVehicle(rental.getRentalId());

            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                System.err.println("Operacja nie powiodła się: " + e.getMessage());
            } finally {
                em.close();
                emf.close();
            }
        }
    }