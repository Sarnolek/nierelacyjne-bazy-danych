package model;

import java.io.Serializable;

public class RentalEvent implements Serializable {
    private Rental rental;
    private String facilityName;

    public RentalEvent(){}

    public RentalEvent(Rental rental, String facilityName){
        this.rental = rental;
        this.facilityName = facilityName;

    }

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }
}
