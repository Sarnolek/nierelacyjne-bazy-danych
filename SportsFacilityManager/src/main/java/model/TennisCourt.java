package model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

@BsonDiscriminator
public class TennisCourt extends SportsFacility{

    @BsonProperty("surface")
    private SurfaceType surfaceType;

    @BsonProperty("indoor")
    private boolean isIndoor;

//    public TennisCourt(){ super(); }


    public TennisCourt(String name,
                       double pricePerHour,
                       int capacity,
                       SurfaceType surfaceType,
                       boolean isIndoor) {
        super(name, pricePerHour, capacity);
        this.surfaceType = surfaceType;
        this.isIndoor = isIndoor;
    }

    @BsonCreator
    public TennisCourt(@BsonProperty("surface") SurfaceType surfaceType, @BsonProperty("indoor") boolean isIndoor) {
        super();
        this.surfaceType = surfaceType;
        this.isIndoor = isIndoor;
    }

    public SurfaceType getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(SurfaceType surfaceType) {
        this.surfaceType = surfaceType;
    }

    public boolean isIndoor() {
        return isIndoor;
    }

    public void setIndoor(boolean indoor) {
        isIndoor = indoor;
    }

    @Override
    public String toString() {
        return "TennisCourt{" +
                super.toString() +
                ", surfaceType=" + surfaceType +
                ", isIndoor=" + isIndoor +
                '}';
    }
}
