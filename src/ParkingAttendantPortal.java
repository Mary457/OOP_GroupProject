package org.example;


import Accounts_Vehicles.ParkingAttendant;
import Accounts_Vehicles.enums.PaymentMethod;
import Infrastructure.ParkingLot;
import Tickets_Payments.ParkingRate;
import Tickets_Payments.ParkingTicket;
import Tickets_Payments.PaymentService;

public class ParkingAttendantPortal {
    private String portalId;
    private ParkingLot parkingLot;
    private PaymentService paymentService;
    private ParkingRate parkingRate;
    private ParkingAttendant currentAttendant;

    public ParkingAttendantPortal(String portalId, ParkingLot parkingLot, PaymentService paymentService, ParkingRate parkingRate) {
        if (portalId == null || portalId.trim().isEmpty()) {
            throw new IllegalArgumentException("Portal ID cannot be null or empty");
        }
        if (parkingLot == null) {
            throw new IllegalArgumentException("ParkingLot cannot be null");
        }
        if (paymentService == null) {
            throw new IllegalArgumentException("PaymentService cannot be null");
        }
        if (parkingRate == null) {
            throw new IllegalArgumentException("ParkingRate cannot be null");
        }
        this.portalId = portalId;
        this.parkingLot = parkingLot;
        this.paymentService = paymentService;
        this.parkingRate = parkingRate;
        this.currentAttendant = null;
    }

    public void login(ParkingAttendant attendant) {
        if (attendant == null) {
            throw new IllegalArgumentException("ParkingAttendant cannot be null");
        }
        if (attendant.login(attendant.getEmail())) { // Simplified login
            this.currentAttendant = attendant;
            System.out.println("Attendant " + attendant.getName() + " logged into Portal " + portalId);
            showWelcomeMessage();
        } else {
            throw new SecurityException("Login failed");
        }
    }

    public void logout() {
        if (currentAttendant != null) {
            System.out.println("Attendant " + currentAttendant.getName() + " logged out from Portal " + portalId);
            currentAttendant.logout();
            currentAttendant = null;
        }
    }

    private void showWelcomeMessage() {
        System.out.println("\n" + "*".repeat(40));
        System.out.println("  PARKING ATTENDANT PORTAL " + portalId);
        System.out.println("  Welcome " + currentAttendant.getName());
        System.out.println("*".repeat(40) + "\n");
    }

    public double scanTicket(ParkingTicket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("ParkingTicket cannot be null");
        }
        if (currentAttendant == null) {
            throw new IllegalStateException("No attendant logged in");
        }

        currentAttendant.scanTicket(ticket);

        double hours = ticket.getDurationHours();
        double amount = parkingRate.calculateCharge(hours);

        System.out.println("Ticket: " + ticket.getTicketId());
        System.out.println("Duration: " + String.format("%.2f", hours) + " hours");
        System.out.println("Amount Due: $" + String.format("%.2f", amount));

        return amount;
    }

    public boolean processCashPayment(ParkingTicket ticket, double amountReceived) {
        if (ticket == null) {
            throw new IllegalArgumentException("ParkingTicket cannot be null");
        }
        if (amountReceived < 0) {
            throw new IllegalArgumentException("Amount received cannot be negative");
        }
        if (currentAttendant == null) {
            throw new IllegalStateException("No attendant logged in");
        }

        double hours = ticket.getDurationHours();
        double amountDue = parkingRate.calculateCharge(hours);

        if (amountReceived < amountDue) {
            System.out.println("Insufficient payment. Need $" + String.format("%.2f", amountDue));
            return false;
        }

        double change = amountReceived - amountDue;
        boolean success = currentAttendant.processCashPayment(ticket, amountDue, paymentService);

        if (success) {
            ticket.markAsPaid();
            System.out.println("Payment successful!");
            if (change > 0) {
                System.out.println("Change to return: $" + String.format("%.2f", change));
            }
            printReceipt(ticket, amountDue);
        }

        return success;
    }

    public boolean processCreditCardPayment(ParkingTicket ticket, String cardNumber, String expiry, String cvv) {
        if (ticket == null) {
            throw new IllegalArgumentException("ParkingTicket cannot be null");
        }
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Card number cannot be empty");
        }
        if (currentAttendant == null) {
            throw new IllegalStateException("No attendant logged in");
        }

        double hours = ticket.getDurationHours();
        double amountDue = parkingRate.calculateCharge(hours);

        System.out.println("Processing credit card payment for ticket " + ticket.getTicketId());
        boolean success = paymentService.processPayment(ticket, amountDue, PaymentMethod.CREDIT_CARD);

        if (success) {
            ticket.markAsPaid();
            System.out.println("Credit card payment successful!");
            printReceipt(ticket, amountDue);
        }

        return success;
    }

    private void printReceipt(ParkingTicket ticket, double amount) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("  ATTENDANT PAYMENT RECEIPT");
        System.out.println("=".repeat(40));
        System.out.println("Portal:       " + portalId);
        System.out.println("Attendant:    " + currentAttendant.getName());
        System.out.println("Ticket ID:    " + ticket.getTicketId());
        System.out.println("Amount Paid:  $" + String.format("%.2f", amount));
        System.out.println("=".repeat(40) + "\n");
    }

    public void overrideGateOpen(ParkingTicket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("ParkingTicket cannot be null");
        }
        if (currentAttendant == null) {
            throw new IllegalStateException("No attendant logged in");
        }

        System.out.println("ATTENDANT OVERRIDE: Opening gate for ticket " + ticket.getTicketId());
        System.out.println(">>> GATE OPENING <<<");
        System.out.println("Vehicle may exit.");
        System.out.println(">>> GATE CLOSING <<<\n");
    }

    public void reportIssue(String ticketId, String issueDescription) {
        if (ticketId == null || ticketId.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket ID cannot be empty");
        }
        if (issueDescription == null || issueDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Issue description cannot be empty");
        }
        if (currentAttendant == null) {
            throw new IllegalStateException("No attendant logged in");
        }

        System.out.println("\n" + "!".repeat(40));
        System.out.println("ISSUE REPORTED");
        System.out.println("Ticket ID: " + ticketId);
        System.out.println("Issue: " + issueDescription);
        System.out.println("Reported by: " + currentAttendant.getName());
        System.out.println("Time: " + java.time.LocalDateTime.now());
        System.out.println("!".repeat(40) + "\n");
    }

    public void showParkingStatus() {
        if (currentAttendant == null) {
            throw new IllegalStateException("No attendant logged in");
        }

        System.out.println("\n" + "-".repeat(40));
        System.out.println("PARKING STATUS (via Portal " + portalId + ")");
        System.out.println("-".repeat(40));
        System.out.println("Total Spots: " + parkingLot.getTotalSpots());
        System.out.println("Occupied: " + parkingLot.getTotalOccupiedSpots());
        System.out.println("Available: " + parkingLot.getTotalAvailableSpots());
        System.out.println("Is Full: " + parkingLot.isFull());
        System.out.println("-".repeat(40) + "\n");
    }

    public ParkingAttendant getCurrentAttendant() {
        return currentAttendant;
    }

    public String getPortalId() {
        return portalId;
    }

    @Override
    public String toString() {
        return "ParkingAttendantPortal{portalId='" + portalId + "', attendant=" +
                (currentAttendant != null ? currentAttendant.getName() : "None") + "}";
    }
}