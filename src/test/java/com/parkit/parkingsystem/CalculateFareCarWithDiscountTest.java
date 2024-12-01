package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

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
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // 1h parking
        Date outTime = new Date();
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);

        fareCalculatorService.calculateFare(ticket, true);

        double expectedPrice = Fare.CAR_RATE_PER_HOUR * 0.95; // 5% discount
        assertEquals(expectedPrice,ticket.getPrice(),0, "Should give a 5% discount for regulars in cars.");
    }
}
