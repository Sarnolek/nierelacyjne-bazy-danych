package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Rental {
    private UUID id;
    private UUID clientId;
    private UUID facilityId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Rental(UUID clientId, UUID facilityId, LocalDateTime startTime, LocalDateTime endTime) {
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
