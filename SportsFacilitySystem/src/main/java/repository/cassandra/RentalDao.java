package repository.cassandra;

import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import model.cassandra.RentalByClientEntity;
import model.cassandra.RentalByFacilityEntity;
import model.cassandra.RentalByIdEntity;
import java.util.UUID;

@Dao
public interface RentalDao {

    @Insert
    BoundStatement saveClientRental(RentalByClientEntity rental);

    @Delete(entityClass = RentalByClientEntity.class)
    BoundStatement deleteClientRental(UUID clientId, java.time.Instant startTime, UUID rentalId);

    @Select
    PagingIterable<RentalByClientEntity> findAllByClient(UUID clientId);

    @Insert
    BoundStatement saveFacilityRental(RentalByFacilityEntity rental);

    @Delete(entityClass = RentalByFacilityEntity.class)
    BoundStatement deleteFacilityRental(UUID facilityId, java.time.Instant startTime, UUID rentalId);

    @Select
    PagingIterable<RentalByFacilityEntity> findAllByFacility(UUID facilityId);

    @Insert
    BoundStatement saveRentalById(RentalByIdEntity rental);

    @Delete
    BoundStatement deleteRentalById(RentalByIdEntity rental);

    @Select
    RentalByIdEntity findById(UUID rentalId);
}