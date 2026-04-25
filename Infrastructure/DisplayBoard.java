package Infrastructure;

import Accounts_Vehicles.enums.SpotType;

import java.util.HashMap;
import java.util.Map;

public class DisplayBoard {
    private String floorId;
    private Map<SpotType, Integer> availableSpots;

    public DisplayBoard(String floorId) {
        if (floorId == null || floorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Floor ID cannot be null or empty");
        }
        this.floorId = floorId;
        this.availableSpots = new HashMap<>();
        // Initialize all spot types with 0
        for (SpotType type : SpotType.values()) {
            availableSpots.put(type, 0);
        }
    }

    public void update(Map<SpotType, Integer> availableCounts) {
        if (availableCounts == null) {
            throw new IllegalArgumentException("Available counts cannot be null");
        }
        for (Map.Entry<SpotType, Integer> entry : availableCounts.entrySet()) {
            availableSpots.put(entry.getKey(), entry.getValue());
        }
        show();
    }

    public void updateSpotType(SpotType spotType, int count) {
        if (spotType == null) {
            throw new IllegalArgumentException("Spot type cannot be null");
        }
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }
        availableSpots.put(spotType, count);
    }

    public int getAvailableCount(SpotType spotType) {
        if (spotType == null) {
            throw new IllegalArgumentException("Spot type cannot be null");
        }
        return availableSpots.getOrDefault(spotType, 0);
    }

    public void show() {
        System.out.println("=========================================");
        System.out.println("  DISPLAY BOARD - FLOOR " + floorId);
        System.out.println("=========================================");
        System.out.printf("%-15s | %s%n", "SPOT TYPE", "AVAILABLE");
        System.out.println("-----------------------------------------");
        for (SpotType type : SpotType.values()) {
            int count = availableSpots.getOrDefault(type, 0);
            System.out.printf("%-15s | %d%n", type.getValue(), count);
        }
        System.out.println("=========================================\n");
    }

    public void showCompact() {
        System.out.print("Floor " + floorId + ": ");
        for (SpotType type : SpotType.values()) {
            System.out.print(type.getValue() + ":" + availableSpots.getOrDefault(type, 0) + " ");
        }
        System.out.println();
    }

    public String getFloorId() {
        return floorId;
    }

    public Map<SpotType, Integer> getAvailableSpots() {
        return new HashMap<>(availableSpots);
    }

    @Override
    public String toString() {
        return "DisplayBoard{floorId='" + floorId + "', availableSpots=" + availableSpots + "}";
    }
}