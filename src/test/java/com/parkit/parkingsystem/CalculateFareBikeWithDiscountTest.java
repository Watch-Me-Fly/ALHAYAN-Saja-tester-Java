package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculateFareBikeWithDiscountTest {
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
        int inMinutes = 60; // set parking duration in minutes here
        inTime.setTime(System.currentTimeMillis() - (inMinutes * 60 * 1000));
        Date outTime = new Date();
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);

        double differenceInMS = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
        double durationInMinutes = differenceInMS / (60 * 1000);
        double expectedRate;

        fareCalculatorService.calculateFare(ticket, true);

        if (durationInMinutes < 30) {
            expectedRate = 0;
        } else {
            double durationInHours = durationInMinutes / 60.0;
            double priceReduced = (Fare.BIKE_RATE_PER_HOUR * durationInHours) * 0.95; // 5% discount
            BigDecimal roundedNumber = new BigDecimal(priceReduced).setScale(2, RoundingMode.HALF_UP);
            expectedRate = roundedNumber.doubleValue();
        }
        assertEquals(expectedRate, ticket.getPrice(), 0.01, "Should give a 5% discount for regulars on bikes.");
    }
}
