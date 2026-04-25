package Infrastructure;


import Accounts_Vehicles.enums.SpotType;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class ParkingFloor {
    private String floorId;
    private Map<String, ParkingSpot> spots;
    private DisplayBoard displayBoard;

    public ParkingFloor(String floorId) {
        if (floorId == null || floorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Floor ID cannot be null or empty");
        }
        this.floorId = floorId;
        this.spots = new HashMap<>();
        this.displayBoard = new DisplayBoard(floorId);
    }

    public ParkingFloor(String floorId, int numberOfSpots) {
        this(floorId);
        initializeSpots(numberOfSpots);
    }

    private void initializeSpots(int numberOfSpots) {
        if (numberOfSpots <= 0) {
            throw new IllegalArgumentException("Number of spots must be positive");
        }

        SpotType[] types = SpotType.values();
        for (int i = 0; i < numberOfSpots; i++) {
            SpotType type = types[i % types.length];
            String spotId = floorId + "_S" + String.format("%03d", i + 1);
            addSpot(new ParkingSpot(spotId, floorId, type));
        }
        System.out.println("Initialized " + numberOfSpots + " spots on floor " + floorId);
    }

    public void addSpot(ParkingSpot spot) {
        if (spot == null) {
            throw new IllegalArgumentException("ParkingSpot cannot be null");
        }
        if (!spot.getFloorId().equals(floorId)) {
            throw new IllegalArgumentException("Spot floor ID does not match this floor");
        }
        if (spots.containsKey(spot.getSpotId())) {
            throw new IllegalStateException("Spot " + spot.getSpotId() + " already exists on this floor");
        }
        spots.put(spot.getSpotId(), spot);
        updateDisplayBoard();
    }

    public void removeSpot(String spotId) {
        if (spotId == null || spotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Spot ID cannot be null or empty");
        }
        ParkingSpot spot = spots.get(spotId);
        if (spot == null) {
            throw new IllegalArgumentException("Spot " + spotId + " not found");
        }
        if (!spot.isAvailable()) {
            throw new IllegalStateException("Cannot remove occupied spot " + spotId);
        }
        spots.remove(spotId);
        updateDisplayBoard();
        System.out.println("Removed spot " + spotId + " from floor " + floorId);
    }

    public ParkingSpot getAvailableSpot(String spotTypeValue) {
        if (spotTypeValue == null || spotTypeValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Spot type cannot be null or empty");
        }

        SpotType targetType = null;
        for (SpotType type : SpotType.values()) {
            if (type.getValue().equalsIgnoreCase(spotTypeValue) ||
                    type.name().equalsIgnoreCase(spotTypeValue)) {
                targetType = type;
                break;
            }
        }

        if (targetType == null) {
            throw new IllegalArgumentException("Invalid spot type: " + spotTypeValue);
        }

        return getAvailableSpot(targetType);
    }

    public ParkingSpot getAvailableSpot(SpotType spotType) {
        if (spotType == null) {
            throw new IllegalArgumentException("Spot type cannot be null");
        }

        for (ParkingSpot spot : spots.values()) {
            if (spot.isAvailable() && spot.getSpotType() == spotType) {
                return spot;
            }
        }
        return null;
    }

    public List<ParkingSpot> getAllAvailableSpots() {
        List<ParkingSpot> availableSpots = new ArrayList<>();
        for (ParkingSpot spot : spots.values()) {
            if (spot.isAvailable()) {
                availableSpots.add(spot);
            }
        }
        return availableSpots;
    }

    public List<ParkingSpot> getAllOccupiedSpots() {
        List<ParkingSpot> occupiedSpots = new ArrayList<>();
        for (ParkingSpot spot : spots.values()) {
            if (!spot.isAvailable()) {
                occupiedSpots.add(spot);
            }
        }
        return occupiedSpots;
    }

    public void updateDisplayBoard() {
        Map<SpotType, Integer> availableCounts = new HashMap<>();

        for (SpotType type : SpotType.values()) {
            availableCounts.put(type, 0);
        }

        for (ParkingSpot spot : spots.values()) {
            if (spot.isAvailable()) {
                availableCounts.put(spot.getSpotType(),
                        availableCounts.get(spot.getSpotType()) + 1);
            }
        }

        displayBoard.update(availableCounts);
    }

    public int getTotalSpots() {
        return spots.size();
    }

    public int getOccupiedSpotsCount() {
        int count = 0;
        for (ParkingSpot spot : spots.values()) {
            if (!spot.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    public int getAvailableSpotsCount() {
        int count = 0;
        for (ParkingSpot spot : spots.values()) {
            if (spot.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    public boolean isFull() {
        return getAvailableSpotsCount() == 0;
    }

    public boolean isEmpty() {
        return getOccupiedSpotsCount() == 0;
    }

    // Getters
    public String getFloorId() {
        return floorId;
    }

    public Map<String, ParkingSpot> getSpots() {
        return new HashMap<>(spots);
    }

    public ParkingSpot getSpot(String spotId) {
        return spots.get(spotId);
    }

    public DisplayBoard getDisplayBoard() {
        return displayBoard;
    }

    public void showStatus() {
        System.out.println("\n--- FLOOR " + floorId + " STATUS ---");
        System.out.println("Total Spots: " + getTotalSpots());
        System.out.println("Occupied: " + getOccupiedSpotsCount());
        System.out.println("Available: " + getAvailableSpotsCount());
        displayBoard.showCompact();
    }

    @Override
    public String toString() {
        return "ParkingFloor{" +
                "floorId='" + floorId + '\'' +
                ", totalSpots=" + spots.size() +
                ", occupied=" + getOccupiedSpotsCount() +
                '}';
    }
}
