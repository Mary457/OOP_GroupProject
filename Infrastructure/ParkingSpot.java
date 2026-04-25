package Infrastructure;

import Accounts_Vehicles.Vehicle;
import Accounts_Vehicles.enums.SpotType;

import java.util.Objects;

public class ParkingSpot {
    private String spotId;
    private String floorId;
    private SpotType spotType;
    private boolean isAvailable;
    private Vehicle vehicle;

    public ParkingSpot(String spotId, String floorId, SpotType spotType) {
        if (spotId == null || spotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Spot ID cannot be null or empty");
        }
        if (floorId == null || floorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Floor ID cannot be null or empty");
        }
        if (spotType == null) {
            throw new IllegalArgumentException("Spot type cannot be null");
        }
        this.spotId = spotId;
        this.floorId = floorId;
        this.spotType = spotType;
        this.isAvailable = true;
        this.vehicle = null;
    }

    public String getSpotId() {
        return spotId;
    }

    public String getFloorId() {
        return floorId;
    }

    public SpotType getSpotType() {
        return spotType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void assignVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }
        if (!isAvailable) {
            throw new IllegalStateException("Spot " + spotId + " is already occupied");
        }
        this.vehicle = vehicle;
        this.isAvailable = false;
        System.out.println("Vehicle " + vehicle.getLicensePlate() + " assigned to spot " + spotId);
    }

    public void removeVehicle() {
        if (isAvailable) {
            throw new IllegalStateException("Spot " + spotId + " is already empty");
        }
        System.out.println("Vehicle " + vehicle.getLicensePlate() + " removed from spot " + spotId);
        this.vehicle = null;
        this.isAvailable = true;
    }

    @Override
    public String toString() {
        return "ParkingSpot{" +
                "spotId='" + spotId + '\'' +
                ", floorId='" + floorId + '\'' +
                ", spotType=" + spotType +
                ", isAvailable=" + isAvailable +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ParkingSpot that = (ParkingSpot) obj;
        return Objects.equals(spotId, that.spotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spotId);
    }
}