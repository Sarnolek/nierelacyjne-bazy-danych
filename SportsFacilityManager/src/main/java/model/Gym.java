package model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@BsonDiscriminator
public class Gym extends SportsFacility{

    @BsonProperty("area_in_sqm")
    private int areaInSqm;

    @BsonProperty("has_sauna")
    private boolean hasSauna;

// public Gym(){ super(); }

    public Gym(String name,
               double pricePerHour,
               int capacity,
               int areaInSqm,
               boolean hasSauna) {
        super(name, pricePerHour, capacity);
        this.areaInSqm = areaInSqm;
        this.hasSauna = hasSauna;
    }

    @BsonCreator
    public Gym(@BsonProperty("area_in_sqm") int areaInSqm, @BsonProperty("has_sauna") boolean hasSauna) {
        super();
        this.areaInSqm = areaInSqm;
        this.hasSauna = hasSauna;
    }

    public int getAreaInSqm() {
        return areaInSqm;
    }

    public void setAreaInSqm(int areaInSqm) {
        this.areaInSqm = areaInSqm;
    }

    public boolean isHasSauna() {
        return hasSauna;
    }

    public void setHasSauna(boolean hasSauna) {
        this.hasSauna = hasSauna;
    }

    @Override
    public String toString() {
        return "Gym{" +
                super.toString() +
                ", areaInSqm=" + areaInSqm +
                ", hasSauna=" + hasSauna +
                '}';
    }
}
