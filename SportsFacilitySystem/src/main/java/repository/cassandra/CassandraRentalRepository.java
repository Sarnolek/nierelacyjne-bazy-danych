package repository.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.DefaultBatchType;
import model.Rental;
import model.cassandra.RentalByClientEntity;
import model.cassandra.RentalByFacilityEntity;
import model.cassandra.RentalByIdEntity;
import repository.RentalRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CassandraRentalRepository implements RentalRepository {

    private final RentalDao rentalDao;
    private final CqlSession session;

    public CassandraRentalRepository(RentalDao rentalDao, CqlSession session) {
        this.rentalDao = rentalDao;
        this.session = session;
    }

    @Override
    public Rental save(Rental rental) {
        var startInstant = rental.getStartTime().toInstant(ZoneOffset.UTC);
        var endInstant = rental.getEndTime().toInstant(ZoneOffset.UTC);

        RentalByClientEntity byClient = new RentalByClientEntity(
                rental.getClientId(), rental.getId(), rental.getFacilityId(), startInstant, endInstant
        );


        RentalByFacilityEntity byFacility = new RentalByFacilityEntity(
                rental.getFacilityId(), rental.getId(), rental.getClientId(), startInstant, endInstant
        );

        RentalByIdEntity byId = new RentalByIdEntity(
                rental.getId(), rental.getClientId(), rental.getFacilityId(), startInstant, endInstant
        );

        BatchStatement batch = BatchStatement.builder(DefaultBatchType.LOGGED)
                .addStatement(rentalDao.saveClientRental(byClient))
                .addStatement(rentalDao.saveFacilityRental(byFacility))
                .addStatement(rentalDao.saveRentalById(byId))
                .build();

        session.execute(batch);
        return rental;
    }

    @Override
    public Optional<Rental> findById(UUID id) {
        RentalByIdEntity entity = rentalDao.findById(id);

        if (entity == null) {
            return Optional.empty();
        }

        Rental rental = new Rental(
                entity.getClientId(),
                entity.getFacilityId(),
                LocalDateTime.ofInstant(entity.getStartTime(), ZoneOffset.UTC),
                LocalDateTime.ofInstant(entity.getEndTime(), ZoneOffset.UTC)
        );
        rental.setId(entity.getRentalId());

        return Optional.of(rental);
    }

    @Override
    public void deleteById(UUID id) {
        RentalByIdEntity entity = rentalDao.findById(id);

        if (entity != null) {
            BatchStatement batch = BatchStatement.builder(DefaultBatchType.LOGGED)
                    .addStatement(rentalDao.deleteRentalById(entity))
                    .addStatement(rentalDao.deleteClientRental(entity.getClientId(), entity.getStartTime(), id))

                    .addStatement(rentalDao.deleteFacilityRental(entity.getFacilityId(), entity.getStartTime(), id))
                    .build();

            session.execute(batch);
        }
    }

    @Override
    public List<Rental> findAll() {
        throw new UnsupportedOperationException("findAll() jest niezalecane w Cassandrze.");
    }

    @Override
    public List<Rental> findByClientId(UUID clientId) {
        List<Rental> result = new ArrayList<>();
        for (RentalByClientEntity entity : rentalDao.findAllByClient(clientId)) {
            Rental rental = new Rental(
                    entity.getClientId(),
                    entity.getFacilityId(),
                    LocalDateTime.ofInstant(entity.getStartTime(), ZoneOffset.UTC),
                    LocalDateTime.ofInstant(entity.getEndTime(), ZoneOffset.UTC)
            );
            rental.setId(entity.getRentalId());
            result.add(rental);
        }
        return result;
    }

    @Override
    public List<Rental> findByFacilityId(UUID facilityId) {
        List<Rental> result = new ArrayList<>();
        for (RentalByFacilityEntity entity : rentalDao.findAllByFacility(facilityId)) {
            Rental rental = new Rental(
                    entity.getClientId(),
                    entity.getFacilityId(),
                    LocalDateTime.ofInstant(entity.getStartTime(), ZoneOffset.UTC),
                    LocalDateTime.ofInstant(entity.getEndTime(), ZoneOffset.UTC)
            );
            rental.setId(entity.getRentalId());
            result.add(rental);
        }
        return result;
    }
}