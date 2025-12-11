package model.cassandra;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import java.time.Instant;
import java.util.UUID;

@Entity
@CqlName("rentals_by_facility")
public class RentalByFacilityEntity {

    @PartitionKey
    @CqlName("facility_id")
    private UUID facilityId;

    @ClusteringColumn(1)
    @CqlName("start_time")
    private Instant startTime;

    @ClusteringColumn(2)
    @CqlName("rental_id")
    private UUID rentalId;

    @CqlName("client_id")
    private UUID clientId;

    @CqlName("end_time")
    private Instant endTime;

    public RentalByFacilityEntity() {}

    public RentalByFacilityEntity(UUID facilityId, UUID rentalId, UUID clientId, Instant startTime, Instant endTime) {
        this.facilityId = facilityId;
        this.rentalId = rentalId;
        this.clientId = clientId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public UUID getFacilityId() { return facilityId; }
    public void setFacilityId(UUID facilityId) { this.facilityId = facilityId; }
    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }
    public UUID getRentalId() { return rentalId; }
    public void setRentalId(UUID rentalId) { this.rentalId = rentalId; }
    public UUID getClientId() { return clientId; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }
    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }
}