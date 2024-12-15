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
import static org.junit.jupiter.api.Assertions.*;

public class CalculateFareCarWithDiscountTest {
    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    public static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    public void setUpTicket() {
        ticket = new Ticket();
    }

    @Test
    public void carWithDiscount() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);

        Date inTime = new Date();
        int inMinutes = 60; // set parking duration in minutes here
        inTime.setTime(System.currentTimeMillis() - (inMinutes * 60 * 1000));
        Date outTime = new Date();
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);

        // convert milliseconds to minutes to check if eligible for free parking
        double differenceInMS = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
        double durationInMinutes = differenceInMS / (60 * 1000);
        double expectedRate;

        fareCalculatorService.calculateFare(ticket, true);

        if (durationInMinutes < 30) {
            expectedRate = 0;
        } else {
            double durationInHours = durationInMinutes / 60.0;
            expectedRate = (Fare.CAR_RATE_PER_HOUR * durationInHours) * 0.95; // 5% discount
        }

        assertEquals(expectedRate, ticket.getPrice(), 0.01,
                "Should give a 5% discount for regulars in cars.");
    }
}
