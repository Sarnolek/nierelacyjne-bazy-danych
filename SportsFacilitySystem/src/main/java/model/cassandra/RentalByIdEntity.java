package model.cassandra;

import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import java.time.Instant;
import java.util.UUID;

@Entity
@CqlName("rentals_by_id")
public class RentalByIdEntity {

    @PartitionKey
    @CqlName("rental_id")
    private UUID rentalId;

    @CqlName("client_id")
    private UUID clientId;

    @CqlName("facility_id")
    private UUID facilityId;

    @CqlName("start_time")
    private Instant startTime;

    @CqlName("end_time")
    private Instant endTime;

    public RentalByIdEntity() {}

    public RentalByIdEntity(UUID rentalId, UUID clientId, UUID facilityId, Instant startTime, Instant endTime) {
        this.rentalId = rentalId;
        this.clientId = clientId;
        this.facilityId = facilityId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public UUID getRentalId() { return rentalId; }
    public void setRentalId(UUID rentalId) { this.rentalId = rentalId; }
    public UUID getClientId() { return clientId; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }
    public UUID getFacilityId() { return facilityId; }
    public void setFacilityId(UUID facilityId) { this.facilityId = facilityId; }
    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }
    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }
}