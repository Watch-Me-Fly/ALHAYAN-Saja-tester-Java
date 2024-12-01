package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculateFareBikeWithDiscount {
    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    public static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }
    @BeforeEach
    public void setupTicket() {
        ticket = new Ticket();
    }
    @Test
    public void bikeWithDiscount() {
        ParkingSpot parkingSpot = new ParkingSpot(2, ParkingType.BIKE, false);
        ticket.setParkingSpot(parkingSpot);

        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000) ); // 1h
        Date outTime = new Date();
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);

        fareCalculatorService.calculateFare(ticket, true);

        double expectedRate = Fare.BIKE_RATE_PER_HOUR * 0.95; // 5% discount
        assertEquals(expectedRate, ticket.getPrice(),0,"Should give a 5% discount for regulars on bikes.");
    }
}
