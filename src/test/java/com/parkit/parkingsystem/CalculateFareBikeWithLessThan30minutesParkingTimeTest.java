package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculateFareBikeWithLessThan30minutesParkingTimeTest {
    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    public static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }
    @BeforeEach
    public void setUpPerTest() {
        ticket = new Ticket();
    }
    @Test
    public void lessThan30MinBike () {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (25 * 60 * 1000) );
        Date outTime = new Date();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        assertEquals(0, ticket.getPrice());
    }
}
