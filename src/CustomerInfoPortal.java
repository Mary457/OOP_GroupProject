package org.example;


import Accounts_Vehicles.enums.PaymentMethod;
import Accounts_Vehicles.enums.TicketStatus;
import Infrastructure.ParkingFloor;
import Tickets_Payments.ParkingRate;
import Tickets_Payments.ParkingTicket;
import Tickets_Payments.PaymentService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomerInfoPortal {
    private String portalId;
    private int floorNumber;
    private ParkingRate parkingRate;
    private PaymentService paymentService;

    public CustomerInfoPortal(String portalId, int floorNumber, ParkingRate parkingRate, PaymentService paymentService) {
        if (portalId == null || portalId.trim().isEmpty()) {
            throw new IllegalArgumentException("Portal ID cannot be null or empty");
        }
        if (floorNumber < 0) {
            throw new IllegalArgumentException("Floor number cannot be negative");
        }
        if (parkingRate == null) {
            throw new IllegalArgumentException("ParkingRate cannot be null");
        }
        if (paymentService == null) {
            throw new IllegalArgumentException("PaymentService cannot be null");
        }
        this.portalId = portalId;
        this.floorNumber = floorNumber;
        this.parkingRate = parkingRate;
        this.paymentService = paymentService;
    }

    public double scanTicket(ParkingTicket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("ParkingTicket cannot be null");
        }

        System.out.println("\n" + "-".repeat(40));
        System.out.println("Customer Info Portal " + portalId + " (Floor " + floorNumber + ")");
        System.out.println("Scanning ticket: " + ticket.getTicketId());

        if (ticket.getStatus() == TicketStatus.PAID) {
            System.out.println("This ticket has already been paid.");
            System.out.println("You can proceed directly to the exit.");
            return 0;
        }

        double hours = ticket.getDurationHours();
        double amount = parkingRate.calculateCharge(hours);

        System.out.println("Entry Time: " + ticket.getEntryTime());
        System.out.println("Current Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("Duration: " + String.format("%.2f", hours) + " hours");
        System.out.println("Amount Due: $" + String.format("%.2f", amount));
        System.out.println("-".repeat(40) + "\n");

        return amount;
    }

    public boolean payTicket(ParkingTicket ticket, PaymentMethod method) {
        if (ticket == null) {
            throw new IllegalArgumentException("ParkingTicket cannot be null");
        }
        if (method == null) {
            throw new IllegalArgumentException("PaymentMethod cannot be null");
        }

        if (ticket.getStatus() == TicketStatus.PAID) {
            System.out.println("Ticket " + ticket.getTicketId() + " is already paid.");
            return true;
        }

        double hours = ticket.getDurationHours();
        double amount = parkingRate.calculateCharge(hours);

        System.out.println("\nProcessing payment at Customer Info Portal " + portalId);
        boolean success = paymentService.processPayment(ticket, amount, method);

        if (success) {
            ticket.markAsPaid();
            printPaymentConfirmation(ticket, amount);
        } else {
            System.out.println("Payment failed. Please try again or use another payment method.");
        }

        return success;
    }

    public boolean payTicket(ParkingTicket ticket) {
        return payTicket(ticket, PaymentMethod.CREDIT_CARD);
    }

    private void printPaymentConfirmation(ParkingTicket ticket, double amount) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("      PRE-PAYMENT CONFIRMATION");
        System.out.println("=".repeat(50));
        System.out.println("Portal:       " + portalId + " (Floor " + floorNumber + ")");
        System.out.println("Ticket ID:    " + ticket.getTicketId());
        System.out.println("Amount Paid:  $" + String.format("%.2f", amount));
        System.out.println("Payment Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=".repeat(50));
        System.out.println("  Ticket is now PAID");
        System.out.println("  You have 15 minutes to exit");
        System.out.println("=".repeat(50) + "\n");
    }

    public void showFloorInfo(ParkingFloor floor) {
        if (floor == null) {
            throw new IllegalArgumentException("ParkingFloor cannot be null");
        }

        System.out.println("\n" + "=".repeat(40));
        System.out.println("FLOOR " + floorNumber + " INFORMATION");
        System.out.println("=".repeat(40));
        floor.getDisplayBoard().show();
    }

    public void showHelpMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("  CUSTOMER INFO PORTAL - HELP");
        System.out.println("=".repeat(40));
        System.out.println("1. Scan your ticket to see amount due");
        System.out.println("2. Pay with Credit Card or Cash");
        System.out.println("3. After payment, you have 15 minutes to exit");
        System.out.println("4. No need to pay again at exit");
        System.out.println("=".repeat(40) + "\n");
    }

    public String getPortalId() {
        return portalId;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    @Override
    public String toString() {
        return "CustomerInfoPortal{portalId='" + portalId + "', floor=" + floorNumber + "}";
    }
}