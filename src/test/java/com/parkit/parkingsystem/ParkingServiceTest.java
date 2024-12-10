package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    public void setUpPerTest() {
        try {
            String vehicleReg = "ABCDEF";
            // mock user input
            lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleReg);
            lenient().when(inputReaderUtil.readSelection()).thenReturn(1); // set as CAR

            // mock parking spot behaviour
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            lenient().when(parkingSpotDAO.getNextAvailableSlot(parkingSpot.getParkingType())).thenReturn(1);  // slot available
            lenient().when(parkingSpotDAO.updateParking(parkingSpot)).thenReturn(true);

            // mock ticket behaviour
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000))); // 1h ago
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber(vehicleReg);
            lenient().when(ticketDAO.getTicket(vehicleReg)).thenReturn(ticket);
            lenient().when(ticketDAO.updateTicket(ticket)).thenReturn(true);
            lenient().when(ticketDAO.getNbTicket(vehicleReg)).thenReturn(2); // regular customer

            // initialize parking service
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);


        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @DisplayName("Process incoming vehicle")
    @Test
    public void testProcessIncomingVehicle(){
        parkingService.processIncomingVehicle();

        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
        verify(ticketDAO, times(1)).getNbTicket("ABCDEF");
    }

    @DisplayName("A parking spot is successfully retrieved")
    @Test
    public void testGetNextParkingNumberIfAvailable() {
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        assertNotNull(parkingSpot);
        assertEquals(1,parkingSpot.getId());
        assertTrue(parkingSpot.isAvailable());
    }

    @DisplayName("No more parking spots are available")
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {

        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0);
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        assertNull(parkingSpot);
    }

    @DisplayName("Wrong vehicle type, should return 0 spots")
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
        lenient().when(inputReaderUtil.readSelection()).thenReturn(3); // invalid option
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parkingService.getNextParkingNumberIfAvailable();
        });
        assertEquals("Entered input is invalid", exception.getMessage());
    }

    @DisplayName("Process exiting vehicle")
    @Test
    public void processExitingVehicleTest(){
        parkingService.processExitingVehicle();

        verify(ticketDAO, times(1)).getNbTicket("ABCDEF");
        verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
    }

    @DisplayName("UpdateTicket returns false when calling processExitingVehicle()")
    @Test
    public void processExitingVehicleTestUnableUpdate() {
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

        parkingService.processExitingVehicle();

        verify(parkingSpotDAO, never()).updateParking(any(ParkingSpot.class));
    }

}
