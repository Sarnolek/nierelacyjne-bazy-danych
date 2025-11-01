package service;

import model.Client;
import model.Rental;
import model.SportsFacility;
import repository.ClientRepository;
import repository.RentalRepository;
import repository.SportsFacilityRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RentalServiceImpl implements RentalService{
    private final ClientRepository clientRepository;
    private final SportsFacilityRepository sportsFacilityRepository;
    private final RentalRepository rentalRepository;

    public RentalServiceImpl(ClientRepository clientRepository, SportsFacilityRepository sportsFacilityRepository, RentalRepository rentalRepository){
        this.clientRepository = clientRepository;
        this.sportsFacilityRepository = sportsFacilityRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    public Rental rentFacility(UUID clientId, UUID facilityId, LocalDateTime startTime, LocalDateTime endTime) throws RentalException {
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new RentalException("Czas rozpoczęcia wypożyczenia obiektu musi być przed czasem zakończenia");
        }
        if (startTime.isBefore(LocalDateTime.now())){
            throw new RentalException("Nie da się wypożyczyć wstecz");
        }

        Client client = clientRepository.findById(clientId).orElseThrow(() -> new RentalException("Klient o ID: " + clientId + " nie istnieje."));
        SportsFacility sportsFacility = sportsFacilityRepository.findById(facilityId).orElseThrow(() -> new RentalException("Obiekt sportowy o ID: " + facilityId + " nie istnieje."));

        // na PAS trzeba sprawdzić czy klient jest aktywny

        if (!isFacilityAvailable(facilityId, startTime, endTime)){
            throw new RentalException("Obiekt sportowy o ID: " + facilityId + " jest juz wypozyczony.");
            // metoda poniżej to sprawdza
        }

        Rental newRental = new Rental(clientId, facilityId, startTime, endTime);
        return rentalRepository.save(newRental);
    }

    @Override
    public boolean isFacilityAvailable(UUID facilityId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Rental> existingRentals = rentalRepository.findByFacilityId(facilityId);

        for(Rental existing : existingRentals){
            if(existing.overlaps(startTime, endTime)){
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
