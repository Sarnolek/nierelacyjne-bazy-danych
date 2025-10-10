package pl.carrental.client;

public enum ClientType {
    STANDARD(1, 0.0),
    ADVANCED(2, 0.10),
    BUSINESS(3, 0.30);

    private final int maxVehicles;
    private final double discount;

    ClientType(int maxVehicles, double discount){
        this.maxVehicles = maxVehicles;
        this.discount = discount;
    }

    public int getMaxVehicles() {
        return maxVehicles;
    }

    public double getDiscount() {
        return discount;
    }
}
