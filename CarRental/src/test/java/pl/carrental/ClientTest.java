package pl.carrental;

import org.example.carrental.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.carrental.model.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class ClientTest {

    private Client client;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        client = new Client(1L, "Jan", "Kowalski", "jan@example.com", ClientType.TYPE1, 1000.0);
        vehicle = new Truck(1L, "XYZ123", "Volvo", "FH16", 2020, "Blue", false, 200.0, 5000);
    }

    @Test
    void shouldReturnTrueWhenClientHasEnoughBalance() {
        assertTrue(client.hasEnoughBalance(500.0));
    }

    @Test
    void shouldReturnFalseWhenClientHasNotEnoughBalance() {
        assertFalse(client.hasEnoughBalance(1500.0));
    }

    @Test
    void shouldAllowClientToRentWhenUnderLimit() {
        assertTrue(client.hasSlotForRent());
    }


    @Test
    void shouldNotAllowClientToRentWhenAtLimit() {
        for (int i = 0; i < client.getClientType().getMaxVehicles(); i++) {
            client.addRent(new Rental((long) i, client, vehicle, 3));
        }
        assertFalse(client.hasSlotForRent());
    }

    @Test
    void shouldAddAndRemoveRentalProperly() {
        Rental rental = new Rental(1L, client, vehicle, 5);
        client.addRent(rental);

        assertEquals(1, client.getRents().size());
        assertTrue(client.getRents().contains(rental));

        client.removeRent(rental);
        assertEquals(0, client.getRents().size());
    }


    @Test
    void shouldCalculateRentalPriceWithDiscount() {
        Rental rental = new Rental(1L, client, vehicle, 5);
        double expected = vehicle.getDailyPrice() * 5 * (1 - client.getClientType().getDiscount());
        assertEquals(expected, rental.getRentalPrice());
    }


    // czy po zakończeniu wypożyczenia samochód przestaje być wypożyczony

    @Test
    void shouldRentaGonnaEnd(){
        Rental rental = new Rental(1L,client, vehicle, 3);
        for (int i = 0; i < rental.getDurationInDays(); i++) {
            //brak takiej funkcji na razie
        }

    }

    @Test
    void clientListAfterRental(){
        Rental rental = new Rental(1L, client, vehicle, 5);
        client.addRent(rental);

        List<Rental> rentals = client.getRents();
        assertEquals(1, rentals.size());
        assertEquals(rental, rentals.get(0));

        client.removeRent(rental);
        assertEquals(0, client.getRents().size());
        List<Rental> rentalsAfterRent = client.getRents();
        assertEquals(0, rentalsAfterRent.size());
    }

    // Czy klient z małym saldem nie może wypożyczyć drogiego auta
    @Test
    void cantRentWhenClientHasNotEnoughBalance() {
        Client client1 = new Client(2L, "Mateusz","Brzeczyszczykiewicz", "monkey@gmail.com", ClientType.TYPE1, 100.0 );
        Rental rental = new Rental(1L, client1, vehicle, 2);
        //canRent sprawdza tylko czy dany klient ma jescze slota na wypozyczenie kolejnego auta, a nie na to czy np go stać na to
        client1.addRent(rental);
        List<Rental> rentals = client1.getRents();
        assertEquals(0, rentals.size());




    }
    @Test
    void isPriceCalculatedRightWithoutDiscount(){
    //ten test chyba nie ma sensu
    }

}