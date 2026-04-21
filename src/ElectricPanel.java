package org.example;


import Accounts_Vehicles.ElectricCar;
import Accounts_Vehicles.enums.PaymentMethod;
import Tickets_Payments.ParkingRate;
import Tickets_Payments.ParkingTicket;
import Tickets_Payments.PaymentService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ElectricPanel {
    private String panelId;
    private String spotId;
    private boolean isAvailable;
    private double chargingRatePerHour;
    private ParkingRate parkingRate;
    private PaymentService paymentService;

    public ElectricPanel(String panelId, String spotId, double chargingRatePerHour,
                         ParkingRate parkingRate, PaymentService paymentService) {
        if (panelId == null || panelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Panel ID cannot be null or empty");
        }
        if (spotId == null || spotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Spot ID cannot be null or empty");
        }
        if (chargingRatePerHour < 0) {
            throw new IllegalArgumentException("Charging rate cannot be negative");
        }
        if (parkingRate == null) {
            throw new IllegalArgumentException("ParkingRate cannot be null");
        }
        if (paymentService == null) {
            throw new IllegalArgumentException("PaymentService cannot be null");
        }
        this.panelId = panelId;
        this.spotId = spotId;
        this.chargingRatePerHour = chargingRatePerHour;
        this.parkingRate = parkingRate;
        this.paymentService = paymentService;
        this.isAvailable = true;
    }

    public double connectAndCharge(ElectricCar electricCar, double hoursToCharge) {
        if (electricCar == null) {
            throw new IllegalArgumentException("ElectricCar cannot be null");
        }
        if (hoursToCharge <= 0) {
            throw new IllegalArgumentException("Hours to charge must be positive");
        }
        if (!isAvailable) {
            throw new IllegalStateException("Electric panel " + panelId + " is currently in use");
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("  ELECTRIC VEHICLE CHARGING");
        System.out.println("=".repeat(50));
        System.out.println("Panel ID: " + panelId);
        System.out.println("Spot ID: " + spotId);
        System.out.println("Vehicle: " + electricCar.getLicensePlate());
        System.out.println("Current Battery: " + electricCar.getBatteryLevel() + "%");

        isAvailable = false;

        double chargingCost = hoursToCharge * chargingRatePerHour;
        System.out.println("Requested Charge Time: " + hoursToCharge + " hours");
        System.out.println("Charging Cost: $" + String.format("%.2f", chargingCost));

        // Simulate charging
        simulateCharging(electricCar, hoursToCharge);

        System.out.println("New Battery Level: " + electricCar.getBatteryLevel() + "%");
        System.out.println("=".repeat(50) + "\n");

        return chargingCost;
    }

    private void simulateCharging(ElectricCar electricCar, double hours) {
        int chargeAmount = (int) (hours * 20); // 20% per hour
        electricCar.charge(chargeAmount);
    }

    public void disconnect() {
        System.out.println("Disconnecting electric vehicle from panel " + panelId);
        isAvailable = true;
        System.out.println("Panel " + panelId + " is now available for next customer.\n");
    }

    public double payForChargingAndParking(ParkingTicket ticket, ElectricCar electricCar,
                                           double chargingHours, PaymentMethod method) {
        if (ticket == null) {
            throw new IllegalArgumentException("ParkingTicket cannot be null");
        }
        if (electricCar == null) {
            throw new IllegalArgumentException("ElectricCar cannot be null");
        }
        if (chargingHours <= 0) {
            throw new IllegalArgumentException("Charging hours must be positive");
        }
        if (method == null) {
            throw new IllegalArgumentException("PaymentMethod cannot be null");
        }

        double parkingHours = ticket.getDurationHours();
        double parkingCost = parkingRate.calculateCharge(parkingHours);
        double chargingCost = chargingHours * chargingRatePerHour;
        double totalCost = parkingCost + chargingCost;

        System.out.println("\n" + "=".repeat(50));
        System.out.println("  ELECTRIC PANEL - PAYMENT");
        System.out.println("=".repeat(50));
        System.out.println("Ticket ID: " + ticket.getTicketId());
        System.out.println("Parking Duration: " + String.format("%.2f", parkingHours) + " hours");
        System.out.println("Parking Cost: $" + String.format("%.2f", parkingCost));
        System.out.println("Charging Duration: " + chargingHours + " hours");
        System.out.println("Charging Cost: $" + String.format("%.2f", chargingCost));
        System.out.println("-".repeat(50));
        System.out.println("TOTAL DUE: $" + String.format("%.2f", totalCost));
        System.out.println("=".repeat(50) + "\n");

        boolean success = paymentService.processPayment(ticket, totalCost, method);

        if (success) {
            ticket.markAsPaid();
            printPaymentReceipt(ticket, totalCost, parkingCost, chargingCost);
            disconnect();
        } else {
            System.out.println("Payment failed. Please try again.");
        }

        return totalCost;
    }

    private void printPaymentReceipt(ParkingTicket ticket, double total, double parking, double charging) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("     ELECTRIC PANEL RECEIPT");
        System.out.println("=".repeat(50));
        System.out.println("Panel ID:     " + panelId);
        System.out.println("Spot ID:      " + spotId);
        System.out.println("Ticket ID:    " + ticket.getTicketId());
        System.out.println("Parking Fee:  $" + String.format("%.2f", parking));
        System.out.println("Charging Fee: $" + String.format("%.2f", charging));
        System.out.println("-".repeat(50));
        System.out.println("TOTAL PAID:   $" + String.format("%.2f", total));
        System.out.println("Payment Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=".repeat(50));
        System.out.println("  Thank you for using our EV charging!");
        System.out.println("=".repeat(50) + "\n");
    }

    public void showChargingOptions() {
        System.out.println("\n" + "-".repeat(40));
        System.out.println("ELECTRIC CHARGING OPTIONS");
        System.out.println("-".repeat(40));
        System.out.println("Rate: $" + chargingRatePerHour + " per hour");
        System.out.println("1 hour  - 20% charge - $" + String.format("%.2f", chargingRatePerHour));
        System.out.println("2 hours - 40% charge - $" + String.format("%.2f", chargingRatePerHour * 2));
        System.out.println("3 hours - 60% charge - $" + String.format("%.2f", chargingRatePerHour * 3));
        System.out.println("4 hours - 80% charge - $" + String.format("%.2f", chargingRatePerHour * 4));
        System.out.println("5 hours - 100% charge - $" + String.format("%.2f", chargingRatePerHour * 5));
        System.out.println("-".repeat(40) + "\n");
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public String getPanelId() {
        return panelId;
    }

    public String getSpotId() {
        return spotId;
    }

    public double getChargingRatePerHour() {
        return chargingRatePerHour;
    }

    public void setChargingRatePerHour(double chargingRatePerHour) {
        if (chargingRatePerHour < 0) {
            throw new IllegalArgumentException("Charging rate cannot be negative");
        }
        this.chargingRatePerHour = chargingRatePerHour;
        System.out.println("Charging rate updated to $" + chargingRatePerHour + "/hour");
    }

    @Override
    public String toString() {
        return "ElectricPanel{panelId='" + panelId + "', spotId='" + spotId +
                "', available=" + isAvailable + ", rate=$" + chargingRatePerHour + "/hour}";
    }
}

