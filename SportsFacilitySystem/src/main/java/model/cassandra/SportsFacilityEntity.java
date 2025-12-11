package model.cassandra;

import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import java.util.UUID;

@Entity
@CqlName("facilities")
public class SportsFacilityEntity {

    @PartitionKey
    private UUID id;
    private String name;

    @CqlName("price_per_hour")
    private double pricePerHour;

    private int capacity;

    @CqlName("facility_type")
    private String facilityType;

    @CqlName("surface_type")
    private String surfaceType;

    @CqlName("is_indoor")
    private Boolean isIndoor;

    @CqlName("area_in_sqm")
    private Integer areaInSqm;

    @CqlName("has_sauna")
    private Boolean hasSauna;

    @CqlName("pool_length")
    private Integer poolLength;

    @CqlName("number_of_lanes")
    private Integer numberOfLanes;


    public SportsFacilityEntity() {}

    public SportsFacilityEntity(UUID id, String name, double pricePerHour, int capacity) {
        this.id = id;
        this.name = name;
        this.pricePerHour = pricePerHour;
        this.capacity = capacity;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public String getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(String surfaceType) {
        this.surfaceType = surfaceType;
    }

    public Boolean getIsIndoor() {
        return isIndoor;
    }

    public void setIsIndoor(Boolean isIndoor) {
        this.isIndoor = isIndoor;
    }

    public Integer getAreaInSqm() {
        return areaInSqm;
    }

    public void setAreaInSqm(Integer areaInSqm) {
        this.areaInSqm = areaInSqm;
    }

    public Boolean getHasSauna() {
        return hasSauna;
    }

    public void setHasSauna(Boolean hasSauna) {
        this.hasSauna = hasSauna;
    }

    public Integer getPoolLength() {
        return poolLength;
    }

    public void setPoolLength(Integer poolLength) {
        this.poolLength = poolLength;
    }

    public Integer getNumberOfLanes() {
        return numberOfLanes;
    }

    public void setNumberOfLanes(Integer numberOfLanes) {
        this.numberOfLanes = numberOfLanes;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(double pricePerHour) { this.pricePerHour = pricePerHour; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}