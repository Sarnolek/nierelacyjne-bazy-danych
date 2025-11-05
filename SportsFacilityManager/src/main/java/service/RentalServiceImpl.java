package service;

import com.mongodb.MongoWriteException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import db.MongoDbManager;
import model.Client;
import model.Rental;
import model.SportsFacility;
import repository.ClientRepository;
import repository.RentalRepository;
import repository.SportsFacilityRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RentalServiceImpl implements RentalService{
    private final ClientRepository clientRepository;
    private final SportsFacilityRepository sportsFacilityRepository;
    private final RentalRepository rentalRepository;

    private final MongoClient mongoClient;
    private final MongoCollection<Rental> rentalCollection;
    private final MongoCollection<SportsFacility> facilityCollection;

    public RentalServiceImpl(ClientRepository clientRepository, SportsFacilityRepository sportsFacilityRepository, RentalRepository rentalRepository){
        this.clientRepository = clientRepository;
        this.sportsFacilityRepository = sportsFacilityRepository;
        this.rentalRepository = rentalRepository;

        this.mongoClient = MongoDbManager.getMongoClient();
        this.rentalCollection = MongoDbManager.getDatabase().getCollection("rentals", Rental.class);
        this.facilityCollection = MongoDbManager.getDatabase().getCollection("facilities", SportsFacility.class);
    }

    @Override
    public Rental rentFacility(UUID clientId, UUID facilityId, LocalDateTime startTime, LocalDateTime endTime) throws RentalException {
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new RentalException("Czas rozpoczęcia wypożyczenia obiektu musi być przed czasem zakończenia");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new RentalException("Nie da się wypożyczyć wstecz");
        }

        clientRepository.findById(clientId)
                .orElseThrow(() -> new RentalException("Klient o ID: " + clientId + " nie istnieje."));
        sportsFacilityRepository.findById(facilityId)
                .orElseThrow(() -> new RentalException("Obiekt sportowy o ID: " + facilityId + " nie istnieje."));


        try (ClientSession session = mongoClient.startSession()) {

            session.startTransaction();

            try {

                // 1. NAJPIERW sprawdź dostępność terminu
                if (!isFacilityAvailableInTransaction(session, facilityId, startTime, endTime)) {
                    throw new RentalException("Obiekt sportowy o ID: " + facilityId + " jest juz wypozyczony w tym terminie.");
                }

                // 2. DOPIERO POTEM inkrementuj licznik (jeśli walidacja schematu jest aktywna)
                UpdateResult updateResult = facilityCollection.updateOne(session,
                        Filters.eq("_id", facilityId),
                        Updates.inc("is_rented", 1)
                );

                Rental newRental = new Rental(clientId, facilityId, startTime, endTime);
                rentalCollection.insertOne(session, newRental);

                session.commitTransaction();
                return newRental;

            } catch (MongoWriteException e) {
                session.abortTransaction();

                if (e.getCode() == 121) {
                    throw new RentalException("Obiekt sportowy o ID: " + facilityId + " jest juz wypozyczony (Walidacja schematu).");
                } else {
                    throw new RentalException("Błąd bazy danych przy próbie rezerwacji: " + e.getMessage());
                }
            } catch (Exception e) {
                session.abortTransaction();
                throw new RentalException("Nie udało się wynająć obiektu: " + e.getMessage());
            }
        }
    }


    private boolean isFacilityAvailableInTransaction(ClientSession session, UUID facilityId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Rental> existingRentals = rentalCollection
                .find(session, Filters.eq("facility_id", facilityId))
                .into(new java.util.ArrayList<>());

        for (Rental existing : existingRentals) {
            if (existing.overlaps(startTime, endTime)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isFacilityAvailable(UUID facilityId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Rental> existingRentals = rentalRepository.findByFacilityId(facilityId);
        for (Rental existing : existingRentals) {
            if (existing.overlaps(startTime, endTime)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Rental> getRentalsForClient(UUID clientId) {
        return rentalRepository.findByClientId(clientId);
    }

    @Override
    public List<Rental> getRentalsForFacility(UUID facilityId) {
        return rentalRepository.findByFacilityId(facilityId);
    }
}
