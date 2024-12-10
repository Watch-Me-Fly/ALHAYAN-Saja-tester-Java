package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean discount){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        long durationMillis = outHour - inHour;

        long durationMin = durationMillis / (60 * 1000);

        if (durationMin < 30)
        {
            ticket.setPrice(0.0);
            return;
        }

        double durationHrs = durationMin / 60.0;
        double price = 0;

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR  -> price = durationHrs * Fare.CAR_RATE_PER_HOUR;
            case BIKE -> price = durationHrs * Fare.BIKE_RATE_PER_HOUR;
            default -> throw new IllegalArgumentException("Unkown Parking Type");
        }

        if (discount) {
            price *= 0.95;
        }

        ticket.setPrice(price);
    }

    public void calculateFare(Ticket ticket){
        calculateFare(ticket, false);
    }
}