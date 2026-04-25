package Infrastructure;

import Accounts_Vehicles.enums.SpotType;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class ParkingLot {
    private static ParkingLot instance;
    private String name;
    private String address;
    private Map<String, ParkingFloor> floors;

    private ParkingLot() {
        this.floors = new HashMap<>();
        this.name = "";
        this.address = "";
    }

    public static ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }

    public void setup(String name, String address) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Parking lot name cannot be null or empty");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }
        this.name = name;
        this.address = address;
        System.out.println("Parking Lot '" + name + "' initialized at " + address);
    }

    public void addFloor(ParkingFloor floor) {
        if (floor == null) {
            throw new IllegalArgumentException("ParkingFloor cannot be null");
        }
        if (floors.containsKey(floor.getFloorId())) {
            throw new IllegalStateException("Floor " + floor.getFloorId() + " already exists");
        }
        floors.put(floor.getFloorId(), floor);
        System.out.println("Added floor " + floor.getFloorId() + " to " + name);
    }

    public void removeFloor(String floorId) {
        if (floorId == null || floorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Floor ID cannot be null or empty");
        }
        ParkingFloor floor = floors.get(floorId);
        if (floor == null) {
            throw new IllegalArgumentException("Floor " + floorId + " not found");
        }
        if (floor.getOccupiedSpotsCount() > 0) {
            throw new IllegalStateException("Cannot remove floor " + floorId + " because it has occupied spots");
        }
        floors.remove(floorId);
        System.out.println("Removed floor " + floorId + " from " + name);
    }

    public ParkingFloor getFloor(String floorId) {
        if (floorId == null || floorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Floor ID cannot be null or empty");
        }
        return floors.get(floorId);
    }

    public Map<String, ParkingFloor> getFloors() {
        return new HashMap<>(floors);
    }

    public List<ParkingFloor> getAllFloors() {
        return new ArrayList<>(floors.values());
    }

    public boolean isFull() {
        for (ParkingFloor floor : floors.values()) {
            if (!floor.isFull()) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmpty() {
        for (ParkingFloor floor : floors.values()) {
            if (!floor.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public int getTotalSpots() {
        int total = 0;
        for (ParkingFloor floor : floors.values()) {
            total += floor.getTotalSpots();
        }
        return total;
    }

    public int getTotalOccupiedSpots() {
        int occupied = 0;
        for (ParkingFloor floor : floors.values()) {
            occupied += floor.getOccupiedSpotsCount();
        }
        return occupied;
    }

    public int getTotalAvailableSpots() {
        return getTotalSpots() - getTotalOccupiedSpots();
    }

    public ParkingSpot findAvailableSpot(SpotType spotType) {
        if (spotType == null) {
            throw new IllegalArgumentException("Spot type cannot be null");
        }

        for (ParkingFloor floor : floors.values()) {
            ParkingSpot spot = floor.getAvailableSpot(spotType);
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }

    public ParkingSpot findAvailableSpot(String spotTypeValue) {
        if (spotTypeValue == null || spotTypeValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Spot type cannot be null or empty");
        }

        for (ParkingFloor floor : floors.values()) {
            ParkingSpot spot = floor.getAvailableSpot(spotTypeValue);
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }

    public Map<SpotType, Integer> getGlobalAvailableSpots() {
        Map<SpotType, Integer> globalAvailable = new HashMap<>();

        for (SpotType type : SpotType.values()) {
            globalAvailable.put(type, 0);
        }

        for (ParkingFloor floor : floors.values()) {
            Map<SpotType, Integer> floorAvailable = floor.getDisplayBoard().getAvailableSpots();
            for (SpotType type : SpotType.values()) {
                globalAvailable.put(type,
                        globalAvailable.get(type) + floorAvailable.getOrDefault(type, 0));
            }
        }

        return globalAvailable;
    }

    public void showFullStatus() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  PARKING LOT: " + name);
        System.out.println("  Address: " + address);
        System.out.println("=".repeat(50));
        System.out.println("Total Floors: " + floors.size());
        System.out.println("Total Spots: " + getTotalSpots());
        System.out.println("Occupied Spots: " + getTotalOccupiedSpots());
        System.out.println("Available Spots: " + getTotalAvailableSpots());
        System.out.println("-".repeat(50));

        Map<SpotType, Integer> globalAvailable = getGlobalAvailableSpots();
        System.out.println("Available Spots by Type:");
        for (SpotType type : SpotType.values()) {
            System.out.println("  " + type.getValue() + ": " + globalAvailable.getOrDefault(type, 0));
        }
        System.out.println("-".repeat(50));

        for (ParkingFloor floor : floors.values()) {
            floor.showStatus();
        }
        System.out.println("=".repeat(50) + "\n");
    }

    public void showDisplayBoards() {
        System.out.println("\n--- ALL DISPLAY BOARDS ---");
        for (ParkingFloor floor : floors.values()) {
            floor.getDisplayBoard().show();
        }
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "ParkingLot{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", floors=" + floors.size() +
                '}';
    }
}