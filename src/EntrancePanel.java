package org.exampe;


import Accounts_Vehicles.Vehicle;
import Infrastructure.ParkingLot;
import Tickets_Payments.ParkingTicket;
import Tickets_Payments.TicketService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EntrancePanel {
    private String panelId;
    private ParkingLot parkingLot;
    private TicketService ticketService;
    private static int ticketCounter = 1;

    public EntrancePanel(String panelId, ParkingLot parkingLot, TicketService ticketService) {
        if (panelId == null || panelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Panel ID cannot be null or empty");
        }
        if (parkingLot == null) {
            throw new IllegalArgumentException("ParkingLot cannot be null");
        }
        if (ticketService == null) {
            throw new IllegalArgumentException("TicketService cannot be null");
        }
        this.panelId = panelId;
        this.parkingLot = parkingLot;
        this.ticketService = ticketService;
    }

    public ParkingTicket printTicket(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }

        if (parkingLot.isFull()) {
            showFullMessage();
            throw new IllegalStateException("Parking lot is full. Cannot issue ticket.");
        }

        String ticketId = generateTicketId();
        ParkingTicket ticket = ticketService.createTicket(ticketId, vehicle, panelId);

        printTicketReceipt(ticket);
        return ticket;
    }

    private String generateTicketId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String ticketNumber = String.format("%05d", ticketCounter++);
        return "TKT_" + timestamp + "_" + ticketNumber;
    }

    private void printTicketReceipt(ParkingTicket ticket) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("        ENTRANCE TICKET");
        System.out.println("=".repeat(50));
        System.out.println("Ticket ID:    " + ticket.getTicketId());
        System.out.println("Entry Time:   " + ticket.getEntryTime());
        System.out.println("Entry Panel:  " + panelId);
        System.out.println("Vehicle:      " + ticket.getVehicle().getLicensePlate());
        System.out.println("Vehicle Type: " + ticket.getVehicle().getVehicleType().getValue());
        System.out.println("=".repeat(50));
        System.out.println("  Please park your vehicle");
        System.out.println("  Display this ticket at exit");
        System.out.println("=".repeat(50) + "\n");
    }

    public void showFullMessage() {
        System.out.println("\n" + "!".repeat(50));
        System.out.println("  SORRY! PARKING LOT IS FULL");
        System.out.println("  Please try again later");
        System.out.println("!".repeat(50) + "\n");
    }

    public void showWelcomeMessage() {
        System.out.println("\n" + "*".repeat(50));
        System.out.println("  WELCOME TO " + parkingLot.getName());
        System.out.println("  Please take a ticket to enter");
        System.out.println("*".repeat(50) + "\n");
    }

    public boolean isParkingAvailable() {
        return !parkingLot.isFull();
    }

    public String getPanelId() {
        return panelId;
    }

    @Override
    public String toString() {
        return "EntrancePanel{panelId='" + panelId + "', isAvailable=" + isParkingAvailable() + "}";
    }
}