package model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public class Rental {
    @BsonId
    private UUID id;

    @BsonProperty("client_id")
    private UUID clientId;

    @BsonProperty("facility_id")
    private UUID facilityId;

    @BsonProperty("start_time")
    private LocalDateTime startTime;

    @BsonProperty("end_time")
    private LocalDateTime endTime;

//    public Rental(){}

        @BsonCreator
    public Rental(@BsonProperty("client_id") UUID clientId,
                  @BsonProperty("facility_id") UUID facilityId,
                  @BsonProperty("start_time") LocalDateTime startTime,
                  @BsonProperty("end_time") LocalDateTime endTime) {
        this.id = UUID.randomUUID();
        this.clientId = clientId;
        this.facilityId = facilityId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public UUID getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(UUID facilityId) {
        this.facilityId = facilityId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean overlaps(LocalDateTime otherStart, LocalDateTime otherEnd) {
        return this.startTime.isBefore(otherEnd) && otherStart.isBefore(this.endTime);
    }

    @Override
    public String toString() {
        return "Rental{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", facilityId=" + facilityId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
