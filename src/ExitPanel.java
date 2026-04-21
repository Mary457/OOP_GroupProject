package org.example;


import Accounts_Vehicles.enums.PaymentMethod;
import Accounts_Vehicles.enums.TicketStatus;
import Infrastructure.ParkingLot;
import Tickets_Payments.ParkingRate;
import Tickets_Payments.ParkingTicket;
import Tickets_Payments.PaymentService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExitPanel {
    private String panelId;
    private ParkingLot parkingLot;
    private PaymentService paymentService;
    private ParkingRate parkingRate;

    public ExitPanel(String panelId, ParkingLot parkingLot, PaymentService paymentService, ParkingRate parkingRate) {
        if (panelId == null || panelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Panel ID cannot be null or empty");
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
        this.panelId = panelId;
        this.parkingLot = parkingLot;
        this.paymentService = paymentService;
        this.parkingRate = parkingRate;
    }

    public double scanTicket(ParkingTicket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("ParkingTicket cannot be null");
        }

        System.out.println("\n" + "-".repeat(40));
        System.out.println("Scanning ticket at Exit Panel " + panelId);
        System.out.println("Ticket ID: " + ticket.getTicketId());

        double hours = ticket.getDurationHours();
        double amount = parkingRate.calculateCharge(hours);

        System.out.println("Entry Time: " + ticket.getEntryTime());
        System.out.println("Current Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("Duration: " + String.format("%.2f", hours) + " hours");
        System.out.println("Total Amount Due: $" + String.format("%.2f", amount));
        System.out.println("-".repeat(40) + "\n");

        return amount;
    }

    public boolean processPayment(ParkingTicket ticket, PaymentMethod method) {
        if (ticket == null) {
            throw new IllegalArgumentException("ParkingTicket cannot be null");
        }
        if (method == null) {
            throw new IllegalArgumentException("PaymentMethod cannot be null");
        }

        if (ticket.getStatus() == TicketStatus.PAID) {
            System.out.println("Ticket " + ticket.getTicketId() + " is already paid. You may exit.");
            return true;
        }

        double hours = ticket.getDurationHours();
        double amount = parkingRate.calculateCharge(hours);

        boolean success = paymentService.processPayment(ticket, amount, method);

        if (success) {
            ticket.markAsPaid();
            printExitReceipt(ticket, amount);
            openGate();
        } else {
            System.out.println("Payment failed. Please try again.");
        }

        return success;
    }

    public boolean processPayment(ParkingTicket ticket) {
        return processPayment(ticket, PaymentMethod.CREDIT_CARD);
    }

    public boolean processCashPayment(ParkingTicket ticket, double cashAmount) {
        if (ticket == null) {
            throw new IllegalArgumentException("ParkingTicket cannot be null");
        }
        if (cashAmount < 0) {
            throw new IllegalArgumentException("Cash amount cannot be negative");
        }

        double hours = ticket.getDurationHours();
        double amount = parkingRate.calculateCharge(hours);

        if (cashAmount < amount) {
            System.out.println("Insufficient cash. Need $" + String.format("%.2f", amount) + ", but received $" + String.format("%.2f", cashAmount));
            return false;
        }

        double change = cashAmount - amount;
        boolean success = paymentService.processCashPayment(ticket, amount);

        if (success) {
            ticket.markAsPaid();
            printExitReceipt(ticket, amount);
            if (change > 0) {
                System.out.println("Change returned: $" + String.format("%.2f", change));
            }
            openGate();
        }

        return success;
    }

    private void printExitReceipt(ParkingTicket ticket, double amount) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         EXIT RECEIPT");
        System.out.println("=".repeat(50));
        System.out.println("Ticket ID:    " + ticket.getTicketId());
        System.out.println("Entry Time:   " + ticket.getEntryTime());
        System.out.println("Exit Time:    " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("Duration:     " + String.format("%.2f", ticket.getDurationHours()) + " hours");
        System.out.println("Amount Paid:  $" + String.format("%.2f", amount));
        System.out.println("Payment Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=".repeat(50));
        System.out.println("  Thank you! Have a great day!");
        System.out.println("=".repeat(50) + "\n");
    }

    private void openGate() {
        System.out.println(">>> GATE OPENING <<<");
        System.out.println("Please exit safely.");
        System.out.println(">>> GATE CLOSING <<<\n");
    }

    public double calculateFee(ParkingTicket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("ParkingTicket cannot be null");
        }
        double hours = ticket.getDurationHours();
        return parkingRate.calculateCharge(hours);
    }

    public String getPanelId() {
        return panelId;
    }

    @Override
    public String toString() {
        return "ExitPanel{panelId='" + panelId + "'}";
    }
}
