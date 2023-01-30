package nwt.kts.backend.service;

import nwt.kts.backend.dto.returnDTO.DeclineDriveReasonDTO;
import nwt.kts.backend.dto.returnDTO.DriveDTO;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.exceptions.DriverNotOnLocationException;
import nwt.kts.backend.exceptions.NonExistingEntityException;
import nwt.kts.backend.repository.DriveRepository;
import nwt.kts.backend.repository.DriverRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.MessagingException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DriveServiceTest {

    @Mock
    private DriveRepository driveRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private DriveService driveService;

    @Test
    @DisplayName("Test driver decline drive driver has future drive")
    public void testDeclineDriveDriverHasFutureDrive() throws MessagingException {
        Passenger passenger = new Passenger();
        passenger.setHasDrive(true);
        passenger.setTokens(0);
        Set<Passenger> passengers = Collections.singleton(passenger);
        Driver driver = new Driver();
        driver.setId(1);
        driver.setAvailable(false);
        driver.setHasFutureDrive(true);
        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1);
        DeclineDriveReasonDTO declineDriveReasonDTO = new DeclineDriveReasonDTO(driveDTO, "Passengers did not show up");
        Drive drive = new Drive();
        drive.setStatus(Status.DRIVING_TO_START);
        drive.setPassengers(passengers);

        when(driveRepository.findDriveById(1)).thenReturn(drive);
        when(driveRepository.save(drive)).thenReturn(drive);

        Drive cancelled = driveService.driverDeclineDrive(driver, declineDriveReasonDTO);
        assertEquals(Status.CANCELLED, cancelled.getStatus());
        assertFalse(driver.isAvailable());
        assertFalse(driver.isHasFutureDrive());
        assertFalse(passenger.getHasDrive());
    }

    @Test
    @DisplayName("Test driver decline drive driver doesn't have future drive")
    public void testDeclineDriveDriverDoesNotHaveFutureDrive() throws MessagingException {
        Passenger passenger = new Passenger();
        passenger.setHasDrive(true);
        passenger.setTokens(0);
        Set<Passenger> passengers = Collections.singleton(passenger);
        Driver driver = new Driver();
        driver.setId(1);
        driver.setAvailable(false);
        driver.setHasFutureDrive(false);
        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1);
        DeclineDriveReasonDTO declineDriveReasonDTO = new DeclineDriveReasonDTO(driveDTO, "Passengers did not show up");
        Drive drive = new Drive();
        drive.setStatus(Status.DRIVING_TO_START);
        drive.setPassengers(passengers);

        when(driveRepository.findDriveById(1)).thenReturn(drive);
        when(driveRepository.save(drive)).thenReturn(drive);

        Drive cancelled = driveService.driverDeclineDrive(driver, declineDriveReasonDTO);
        assertEquals(Status.CANCELLED, cancelled.getStatus());
        assertTrue(driver.isAvailable());
        assertFalse(driver.isHasFutureDrive());
        assertFalse(passenger.getHasDrive());
    }

    @Test
    @DisplayName("Test driver decline drive passenger refunds")
    public void testDeclineDriveDriverPassengerRefunds() throws MessagingException {
        Passenger passenger = new Passenger();
        passenger.setHasDrive(true);
        passenger.setTokens(0);
        Set<Passenger> passengers = Collections.singleton(passenger);
        Driver driver = new Driver();
        driver.setId(1);
        driver.setHasFutureDrive(false);
        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1);
        DeclineDriveReasonDTO declineDriveReasonDTO = new DeclineDriveReasonDTO(driveDTO, "Passengers did not show up");
        Drive drive = new Drive();
        drive.setPrice(50.0);
        drive.setStatus(Status.DRIVING_TO_START);
        drive.setPassengers(passengers);

        when(driveRepository.findDriveById(1)).thenReturn(drive);
        when(driveRepository.save(drive)).thenReturn(drive);

        Drive cancelled = driveService.driverDeclineDrive(driver, declineDriveReasonDTO);
        assertEquals(Status.CANCELLED, cancelled.getStatus());
        assertTrue(driver.isAvailable());
        assertFalse(driver.isHasFutureDrive());
        assertFalse(passenger.getHasDrive());
        assertEquals(50, passenger.getTokens());
    }

    @Test
    @DisplayName("Test driver decline drive all passengers refund")
    public void testDeclineDriveDriverAllPassengersRefund() throws MessagingException {
        Passenger passenger1 = new Passenger();
        passenger1.setHasDrive(true);
        passenger1.setTokens(0);
        Passenger passenger2 = new Passenger();
        passenger2.setHasDrive(true);
        passenger2.setTokens(0);
        Set<Passenger> passengers = new HashSet<>(Arrays.asList(passenger1, passenger2));
        Driver driver = new Driver();
        driver.setId(1);
        driver.setHasFutureDrive(false);
        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1);
        DeclineDriveReasonDTO declineDriveReasonDTO = new DeclineDriveReasonDTO(driveDTO, "Passengers did not show up");
        Drive drive = new Drive();
        drive.setPrice(50.0);
        drive.setStatus(Status.DRIVING_TO_START);
        drive.setPassengers(passengers);

        when(driveRepository.findDriveById(1)).thenReturn(drive);
        when(driveRepository.save(drive)).thenReturn(drive);

        Drive cancelled = driveService.driverDeclineDrive(driver, declineDriveReasonDTO);
        assertEquals(Status.CANCELLED, cancelled.getStatus());
        assertTrue(driver.isAvailable());
        assertFalse(driver.isHasFutureDrive());
        assertFalse(passenger1.getHasDrive());
        assertFalse(passenger2.getHasDrive());
        assertEquals(25, passenger1.getTokens());
        assertEquals(25, passenger2.getTokens());
    }

    @Test
    @DisplayName("Test throw NonExistingEntityException because drive is null")
    public void testStartDriveThrowNonExistingEntityExceptionBecauseDriveIsNull() {
        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(2);

        when(driveRepository.findDriveById(2)).thenReturn(null);
        assertThrows(NonExistingEntityException.class, () -> driveService.startDrive(driveDTO));
    }

    @Test
    @DisplayName("Test throw DriverNotOnLocationException because driver is not on location latitude")
    public void testStartDriveThrowDriverNotOnLocationExceptionBecauseDriverIsNotOnLocationLatitude() {
        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1);
        Route route = new Route();
        Point point1 = new Point(45.238548, 19.848225);
        Point point2 = new Point(45.255055, 19.810161);
        List<Point> waypoints = Arrays.asList(point1, point2);
        route.setWaypoints(waypoints);
        Driver driver = new Driver();
        driver.setLocation(new Point(45.24232, 19.848225));
        Drive drive = new Drive();
        drive.setDriver(driver);
        drive.setRoute(route);

        when(driveRepository.findDriveById(1)).thenReturn(drive);
        assertThrows(DriverNotOnLocationException.class, () -> driveService.startDrive(driveDTO));
    }

    @Test
    @DisplayName("Test throw DriverNotOnLocationException because driver is not on location longitude")
    public void testStartDriveThrowDriverNotOnLocationExceptionBecauseDriverIsNotOnLocationLongitude() {
        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1);
        Route route = new Route();
        Point point1 = new Point(45.238548, 19.848225);
        Point point2 = new Point(45.255055, 19.810161);
        List<Point> waypoints = Arrays.asList(point1, point2);
        route.setWaypoints(waypoints);
        Driver driver = new Driver();
        driver.setLocation(new Point(45.238548, 19.838225));
        Drive drive = new Drive();
        drive.setDriver(driver);
        drive.setRoute(route);

        when(driveRepository.findDriveById(1)).thenReturn(drive);
        assertThrows(DriverNotOnLocationException.class, () -> driveService.startDrive(driveDTO));
    }

    @Test
    @DisplayName("Test start drive driver on location")
    public void testStartDriveDriverOnLocation() {
        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1);
        Route route = new Route();
        Point point1 = new Point(45.238548, 19.848225);
        Point point2 = new Point(45.255055, 19.810161);
        List<Point> waypoints = Arrays.asList(point1, point2);
        route.setWaypoints(waypoints);
        Driver driver = new Driver();
        driver.setLocation(new Point(45.238548, 19.848225));
        Drive drive = new Drive();
        drive.setDriver(driver);
        drive.setRoute(route);

        when(driveRepository.findDriveById(1)).thenReturn(drive);
        when(driveRepository.save(drive)).thenReturn(drive);

        Drive saved = driveService.startDrive(driveDTO);
        assertEquals(Status.STARTED, saved.getStatus());
    }

    @Test
    @DisplayName("Test start drive driver very close to location")
    public void testStartDriveDriverVeryCloseToLocation() {
        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1);
        Route route = new Route();
        Point point1 = new Point(45.238548, 19.848225);
        Point point2 = new Point(45.255055, 19.810161);
        List<Point> waypoints = Arrays.asList(point1, point2);
        route.setWaypoints(waypoints);
        Driver driver = new Driver();
        driver.setLocation(new Point(45.238528, 19.848245));
        Drive drive = new Drive();
        drive.setDriver(driver);
        drive.setRoute(route);

        when(driveRepository.findDriveById(1)).thenReturn(drive);
        when(driveRepository.save(drive)).thenReturn(drive);

        Drive saved = driveService.startDrive(driveDTO);
        assertEquals(Status.STARTED, saved.getStatus());
    }

    @Test
    @DisplayName("Test throw NonExistingEntityException because drive is null")
    public void testEndDriveThrowNonExistingEntityExceptionBecauseDriveIsNull() {
        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(2);

        when(driveRepository.findDriveById(2)).thenReturn(null);
        assertThrows(NonExistingEntityException.class, () -> driveService.endDrive(driveDTO));
    }

    @Test
    @DisplayName("Test throw DriverNotOnLocationException because driver is not on location latitude")
    public void testEndDriveThrowDriverNotOnLocationExceptionBecauseDriverIsNotOnLocationLatitude() {
        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1);
        Route route = new Route();
        Point point1 = new Point(45.238548, 19.848225);
        Point point2 = new Point(45.255055, 19.810161);
        List<Point> waypoints = Arrays.asList(point1, point2);
        route.setWaypoints(waypoints);
        Driver driver = new Driver();
        driver.setLocation(new Point(45.24232, 19.810161));
        Drive drive = new Drive();
        drive.setDriver(driver);
        drive.setRoute(route);

        when(driveRepository.findDriveById(1)).thenReturn(drive);
        assertThrows(DriverNotOnLocationException.class, () -> driveService.startDrive(driveDTO));
    }

    @Test
    @DisplayName("Test throw DriverNotOnLocationException because driver is not on location longitude")
    public void testEndDriveThrowDriverNotOnLocationExceptionBecauseDriverIsNotOnLocationLongitude() {
        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1);
        Route route = new Route();
        Point point1 = new Point(45.238548, 19.848225);
        Point point2 = new Point(45.255055, 19.810161);
        List<Point> waypoints = Arrays.asList(point1, point2);
        route.setWaypoints(waypoints);
        Driver driver = new Driver();
        driver.setLocation(new Point(45.255055, 19.838225));
        Drive drive = new Drive();
        drive.setDriver(driver);
        drive.setRoute(route);

        when(driveRepository.findDriveById(1)).thenReturn(drive);
        assertThrows(DriverNotOnLocationException.class, () -> driveService.startDrive(driveDTO));
    }

    @Test
    @DisplayName("Test end drive driver has future drive")
    public void testEndDriveDriverHasFutureDrive() {
        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1);
        Route route = new Route();
        Point point1 = new Point(45.238548, 19.848225);
        Point point2 = new Point(45.255055, 19.810161);
        List<Point> waypoints = Arrays.asList(point1, point2);
        route.setWaypoints(waypoints);
        Driver driver = new Driver();
        driver.setAvailable(false);
        driver.setHasFutureDrive(true);
        driver.setLocation(new Point(45.255055, 19.810161));
        Passenger passenger = new Passenger();
        passenger.setHasDrive(true);
        Set<Passenger> passengers = Collections.singleton(passenger);
        Drive drive = new Drive();
        drive.setDriver(driver);
        drive.setRoute(route);
        drive.setPassengers(passengers);

        when(driveRepository.findDriveById(1)).thenReturn(drive);
        when(driveRepository.save(drive)).thenReturn(drive);

        Drive saved = driveService.endDrive(driveDTO);
        assertEquals(Status.FINISHED, saved.getStatus());
        assertFalse(driver.isAvailable());
        assertFalse(driver.isHasFutureDrive());
        assertFalse(passenger.getHasDrive());
        assertNotNull(saved.getEndDate());
    }

    @Test
    @DisplayName("Test end drive driver doesn't have future drive")
    public void testEndDriveDriverDoesNotHaveFutureDrive() {
        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1);
        Route route = new Route();
        Point point1 = new Point(45.238548, 19.848225);
        Point point2 = new Point(45.255055, 19.810161);
        List<Point> waypoints = Arrays.asList(point1, point2);
        route.setWaypoints(waypoints);
        Driver driver = new Driver();
        driver.setAvailable(false);
        driver.setHasFutureDrive(false);
        driver.setLocation(new Point(45.255055, 19.810161));
        Passenger passenger = new Passenger();
        passenger.setHasDrive(true);
        Set<Passenger> passengers = Collections.singleton(passenger);
        Drive drive = new Drive();
        drive.setDriver(driver);
        drive.setRoute(route);
        drive.setPassengers(passengers);

        when(driveRepository.findDriveById(1)).thenReturn(drive);
        when(driveRepository.save(drive)).thenReturn(drive);

        Drive saved = driveService.endDrive(driveDTO);
        assertEquals(Status.FINISHED, saved.getStatus());
        assertTrue(driver.isAvailable());
        assertFalse(driver.isHasFutureDrive());
        assertFalse(passenger.getHasDrive());
        assertNotNull(saved.getEndDate());
    }
}
