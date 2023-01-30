package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.TempDriveDTO;
import nwt.kts.backend.dto.returnDTO.DeclineDriveReasonDTO;
import nwt.kts.backend.dto.returnDTO.DriveDTO;
import nwt.kts.backend.dto.returnDTO.RouteDTO;
import nwt.kts.backend.dto.returnDTO.TypeDTO;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.exceptions.*;
import nwt.kts.backend.repository.DriveRepository;
import nwt.kts.backend.repository.TempDriveRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DriveServiceTest {

    @Mock
    private TempDriveRepository tempDriveRepository;
    @Mock
    private DriveRepository driveRepository;
    @Mock
    private PassengerService passengerService;
    @Mock
    private TypeService typeService;
    @Mock
    private RouteService routeService;
    @Mock
    private DriverService driverService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private DriveService driveService;

    @Test
    @DisplayName("Should return EntityNotFoundException when there is an invalid email in the set")
    public void testPassengerEmailsNotExisting() {
        TempDriveDTO tempDriveDTO = new TempDriveDTO();
        tempDriveDTO.setEmails(new HashSet<>());
        tempDriveDTO.getEmails().add("darko.darkovic@gmail.com");
        tempDriveDTO.getEmails().add("ne.postojim@gmail.com");

        when(passengerService.allPassengersExist(tempDriveDTO.getEmails())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> driveService.saveTempDrive(tempDriveDTO));
        verify(passengerService, times(1)).allPassengersExist(tempDriveDTO.getEmails());
    }

    @Test
    @DisplayName("Should return PassengerHasDriveException if one of the passengers has a drive")
    public void testPassengersHavingDrives() {
        TempDriveDTO tempDriveDTO = new TempDriveDTO();
        tempDriveDTO.setEmails(new HashSet<>());
        tempDriveDTO.getEmails().add("darko.darkovic@gmail.com");
        Passenger passenger;

        when(passengerService.allPassengersExist(tempDriveDTO.getEmails())).thenReturn(true);
        when(passengerService.findPassengerByEmail("darko.darkovic@gmail.com")).thenReturn(passenger = new Passenger());
        passenger.setHasDrive(true);

        assertThrows(PassengerHasDriveException.class, () -> driveService.saveTempDrive(tempDriveDTO));
        verify(passengerService, times(1)).allPassengersExist(tempDriveDTO.getEmails());
        verify(passengerService, times(1)).findPassengerByEmail("darko.darkovic@gmail.com");
    }

    @Test
    @DisplayName("Should return InvalidStartTimeException if drive is requested for the past time")
    public void testInvalidStartTimeForPast() {
        TempDriveDTO tempDriveDTO = new TempDriveDTO();
        tempDriveDTO.setEmails(new HashSet<>());
        tempDriveDTO.getEmails().add("darko.darkovic@gmail.com");
        tempDriveDTO.setStartDate(createPastTime());
        Passenger passenger;

        when(passengerService.allPassengersExist(tempDriveDTO.getEmails())).thenReturn(true);
        when(passengerService.findPassengerByEmail("darko.darkovic@gmail.com")).thenReturn(passenger = new Passenger());
        passenger.setHasDrive(false);
        InvalidStartTimeException exception = assertThrows(InvalidStartTimeException.class, () -> driveService.saveTempDrive(tempDriveDTO));
        assertEquals("Can't order drive for the past!", exception.getMessage());
        verify(passengerService, times(1)).allPassengersExist(tempDriveDTO.getEmails());
        verify(passengerService, times(1)).findPassengerByEmail("darko.darkovic@gmail.com");
    }

    @Test
    @DisplayName("Should return InvalidStartTimeException if drive is requested for the drive more than 5 hours away")
    public void testInvalidStartTimeForFuture() {
        TempDriveDTO tempDriveDTO = new TempDriveDTO();
        tempDriveDTO.setEmails(new HashSet<>());
        tempDriveDTO.getEmails().add("darko.darkovic@gmail.com");
        tempDriveDTO.setStartDate(createMoreThanFiveHourTime());
        Passenger passenger;

        when(passengerService.allPassengersExist(tempDriveDTO.getEmails())).thenReturn(true);
        when(passengerService.findPassengerByEmail("darko.darkovic@gmail.com")).thenReturn(passenger = new Passenger());
        passenger.setHasDrive(false);
        InvalidStartTimeException exception = assertThrows(InvalidStartTimeException.class, () -> driveService.saveTempDrive(tempDriveDTO));
        assertEquals("Can't order drive more than 5 hours in advance!", exception.getMessage());
        verify(passengerService, times(1)).allPassengersExist(tempDriveDTO.getEmails());
        verify(passengerService, times(1)).findPassengerByEmail("darko.darkovic@gmail.com");
    }

    @Test
    @DisplayName("Should return TempDrive with status pending using start time 19 minutes in the future")
    public void testReturnTempDriveWithStatusPendingJustBeforeReservationTime() {
        TempDriveDTO tempDriveDTO = new TempDriveDTO();
        tempDriveDTO.setEmails(new HashSet<>());
        tempDriveDTO.setTags(new HashSet<>());
        tempDriveDTO.getEmails().add("darko.darkovic@gmail.com");
        tempDriveDTO.setStartDate(createTimeJustBeforeReservationTime());
        tempDriveDTO.setTypeDTO(new TypeDTO(1, "SUV"));
        RouteDTO routeDTO = new RouteDTO();
        tempDriveDTO.setRouteDTO(routeDTO);
        tempDriveDTO.getRouteDTO().setWaypoints(new ArrayList<>());
        Passenger passenger;

        when(passengerService.allPassengersExist(tempDriveDTO.getEmails())).thenReturn(true);
        when(passengerService.findPassengerByEmail("darko.darkovic@gmail.com")).thenReturn(passenger = new Passenger());
        when(typeService.findTypeByName(tempDriveDTO.getTypeDTO().getName())).thenReturn(new Type(1, "SUV"));
        passenger.setHasDrive(false);

        when(routeService.saveRoute(Mockito.any(Route.class))).thenAnswer(i -> i.getArguments()[0]);
        when(tempDriveRepository.save(Mockito.any(TempDrive.class))).thenAnswer(i -> i.getArguments()[0]);

        TempDrive tempDriveActual = driveService.saveTempDrive(tempDriveDTO);

        assertEquals(Status.PENDING, tempDriveActual.getStatus());
        verify(passengerService, times(1)).allPassengersExist(tempDriveDTO.getEmails());
        verify(passengerService, times(1)).findPassengerByEmail("darko.darkovic@gmail.com");
        verify(typeService, times(1)).findTypeByName(tempDriveDTO.getTypeDTO().getName());
        verify(routeService, times(1)).saveRoute(tempDriveActual.getRoute());
    }

    @Test
    @DisplayName("Should return TempDrive with status reserved using start time 20 minutes in future")
    public void testReturnTempDriveWithStatusReservedJustInTimeForReservation() {
        TempDriveDTO tempDriveDTO = new TempDriveDTO();
        tempDriveDTO.setEmails(new HashSet<>());
        tempDriveDTO.setTags(new HashSet<>());
        tempDriveDTO.getEmails().add("darko.darkovic@gmail.com");
        tempDriveDTO.setStartDate(createTimeJustInTimeForReservationTime());
        tempDriveDTO.setTypeDTO(new TypeDTO(1, "SUV"));
        RouteDTO routeDTO = new RouteDTO();
        tempDriveDTO.setRouteDTO(routeDTO);
        tempDriveDTO.getRouteDTO().setWaypoints(new ArrayList<>());
        Passenger passenger;

        when(passengerService.allPassengersExist(tempDriveDTO.getEmails())).thenReturn(true);
        when(passengerService.findPassengerByEmail("darko.darkovic@gmail.com")).thenReturn(passenger = new Passenger());
        when(typeService.findTypeByName(tempDriveDTO.getTypeDTO().getName())).thenReturn(new Type(1, "SUV"));
        passenger.setHasDrive(false);

        when(routeService.saveRoute(Mockito.any(Route.class))).thenAnswer(i -> i.getArguments()[0]);
        when(tempDriveRepository.save(Mockito.any(TempDrive.class))).thenAnswer(i -> i.getArguments()[0]);

        TempDrive tempDriveActual = driveService.saveTempDrive(tempDriveDTO);

        assertEquals(Status.RESERVED, tempDriveActual.getStatus());
        verify(passengerService, times(1)).allPassengersExist(tempDriveDTO.getEmails());
        verify(passengerService, times(1)).findPassengerByEmail("darko.darkovic@gmail.com");
        verify(typeService, times(1)).findTypeByName(tempDriveDTO.getTypeDTO().getName());
        verify(routeService, times(1)).saveRoute(tempDriveActual.getRoute());
    }

    @Test
    @DisplayName("Should return TempDrive with status pending using current start time")
    public void testReturnTempDriveWithStatusPendingUsingCurrentTime() {
        TempDriveDTO tempDriveDTO = new TempDriveDTO();
        tempDriveDTO.setEmails(new HashSet<>());
        tempDriveDTO.setTags(new HashSet<>());
        tempDriveDTO.getEmails().add("darko.darkovic@gmail.com");
        tempDriveDTO.setStartDate(createCurrentTime());
        tempDriveDTO.setTypeDTO(new TypeDTO(1, "SUV"));
        RouteDTO routeDTO = new RouteDTO();
        tempDriveDTO.setRouteDTO(routeDTO);
        tempDriveDTO.getRouteDTO().setWaypoints(new ArrayList<>());
        Passenger passenger;

        when(passengerService.allPassengersExist(tempDriveDTO.getEmails())).thenReturn(true);
        when(passengerService.findPassengerByEmail("darko.darkovic@gmail.com")).thenReturn(passenger = new Passenger());
        when(typeService.findTypeByName(tempDriveDTO.getTypeDTO().getName())).thenReturn(new Type(1, "SUV"));
        passenger.setHasDrive(false);

        when(routeService.saveRoute(Mockito.any(Route.class))).thenAnswer(i -> i.getArguments()[0]);
        when(tempDriveRepository.save(Mockito.any(TempDrive.class))).thenAnswer(i -> i.getArguments()[0]);

        TempDrive tempDriveActual = driveService.saveTempDrive(tempDriveDTO);

        assertEquals(Status.PENDING, tempDriveActual.getStatus());
        verify(passengerService, times(1)).allPassengersExist(tempDriveDTO.getEmails());
        verify(passengerService, times(1)).findPassengerByEmail("darko.darkovic@gmail.com");
        verify(typeService, times(1)).findTypeByName(tempDriveDTO.getTypeDTO().getName());
        verify(routeService, times(1)).saveRoute(tempDriveActual.getRoute());
    }

    @Test
    @DisplayName("Should return TempDrive with status reserved using start time 5 hours ahead")
    public void testReturnTempDriveWithStatusReservedUsingStartTimeFiveHoursAhead() {
        TempDriveDTO tempDriveDTO = new TempDriveDTO();
        tempDriveDTO.setEmails(new HashSet<>());
        tempDriveDTO.setTags(new HashSet<>());
        tempDriveDTO.getEmails().add("darko.darkovic@gmail.com");
        tempDriveDTO.setStartDate(createTimeFiveHoursAhead());
        tempDriveDTO.setTypeDTO(new TypeDTO(1, "SUV"));
        RouteDTO routeDTO = new RouteDTO();
        tempDriveDTO.setRouteDTO(routeDTO);
        tempDriveDTO.getRouteDTO().setWaypoints(new ArrayList<>());
        Passenger passenger;

        when(passengerService.allPassengersExist(tempDriveDTO.getEmails())).thenReturn(true);
        when(passengerService.findPassengerByEmail("darko.darkovic@gmail.com")).thenReturn(passenger = new Passenger());
        when(typeService.findTypeByName(tempDriveDTO.getTypeDTO().getName())).thenReturn(new Type(1, "SUV"));
        passenger.setHasDrive(false);

        when(routeService.saveRoute(Mockito.any(Route.class))).thenAnswer(i -> i.getArguments()[0]);
        when(tempDriveRepository.save(Mockito.any(TempDrive.class))).thenAnswer(i -> i.getArguments()[0]);

        TempDrive tempDriveActual = driveService.saveTempDrive(tempDriveDTO);

        assertEquals(Status.RESERVED, tempDriveActual.getStatus());
        verify(passengerService, times(1)).allPassengersExist(tempDriveDTO.getEmails());
        verify(passengerService, times(1)).findPassengerByEmail("darko.darkovic@gmail.com");
        verify(typeService, times(1)).findTypeByName(tempDriveDTO.getTypeDTO().getName());
        verify(routeService, times(1)).saveRoute(tempDriveActual.getRoute());
    }

    @Test
    @DisplayName("Should return NotEnoughTokensException if a passenger doesn't have enough tokens to pay for the drive")
    public void testShouldReturnDriverNotEnoughTokensExceptionSinglePassenger() {
        Timestamp startDate = new Timestamp(new Date().getTime());
        Passenger passenger = new Passenger();
        passenger.setTokens(599);
        Set<Passenger> passengers = new HashSet<>();
        passengers.add(passenger);
        TempDrive tempDrive = new TempDrive(startDate, 600, 3, new HashSet<>(), passengers, new Route(), new Type());

        when(tempDriveRepository.findTempDriveById(1)).thenReturn(tempDrive);

        NotEnoughTokensException exception = assertThrows(NotEnoughTokensException.class,
                () -> driveService.acceptDriveConsent(1));

        assertEquals("You don't have enough tokens to pay for the ride!", exception.getMessage());
        verify(tempDriveRepository, times(1)).findTempDriveById(1);
    }

    @Test
    @DisplayName("Should return NotEnoughTokensException if passengers don't have enough tokens to pay for the drive")
    public void testShouldReturnDriverNotEnoughTokensExceptionMultiplePassengers() {
        Timestamp startDate = new Timestamp(new Date().getTime());
        Passenger passenger1 = new Passenger();
        passenger1.setTokens(200);
        Passenger passenger2 = new Passenger();
        passenger2.setTokens(300);
        Passenger passenger3 = new Passenger();
        passenger3.setTokens(99);
        Set<Passenger> passengers = new HashSet<>();
        passengers.add(passenger1);
        passengers.add(passenger2);
        passengers.add(passenger3);
        TempDrive tempDrive = new TempDrive(startDate, 600, 3, new HashSet<>(), passengers, new Route(), new Type());
        tempDrive.addAcceptedPassenger();
        tempDrive.addAcceptedPassenger();
        when(tempDriveRepository.findTempDriveById(1)).thenReturn(tempDrive);

        NotEnoughTokensException exception = assertThrows(NotEnoughTokensException.class,
                () -> driveService.acceptDriveConsent(1));

        assertEquals("You don't have enough tokens to pay for the ride!", exception.getMessage());
        verify(tempDriveRepository, times(1)).findTempDriveById(1);
    }

    @Test
    @DisplayName("Should return DriverNotFoundException if there are no available drivers")
    public void testShouldReturnDriverNotFoundExceptionForNoAvailableDrivers() {
        Timestamp startDate = new Timestamp(new Date().getTime());
        Passenger passenger = new Passenger();
        passenger.setTokens(1000);
        Set<Passenger> passengers = new HashSet<>();
        passengers.add(passenger);
        TempDrive tempDrive = new TempDrive(startDate, 600, 3, new HashSet<>(), passengers, new Route(), new Type());

        when(tempDriveRepository.findTempDriveById(1)).thenReturn(tempDrive);
        when(driverService.selectDriverForDrive(tempDrive)).thenReturn(null);

        DriverNotFoundException exception = assertThrows(DriverNotFoundException.class,
                () -> driveService.acceptDriveConsent(1));

        assertEquals("There are no available drivers right now!", exception.getMessage());
        verify(tempDriveRepository, times(1)).findTempDriveById(1);
        verify(driverService, times(1)).selectDriverForDrive(tempDrive);
    }

    @Test
    @DisplayName("Should return TempDrive with one more accepted consent")
    public void testShouldReturnTempDriveWithIncreasedDriveConsent() {
        Timestamp startDate = new Timestamp(new Date().getTime());
        Passenger passenger1 = new Passenger();
        passenger1.setTokens(200);
        Passenger passenger2 = new Passenger();
        passenger2.setTokens(300);
        Passenger passenger3 = new Passenger();
        passenger3.setTokens(99);
        Set<Passenger> passengers = new HashSet<>();
        passengers.add(passenger1);
        passengers.add(passenger2);
        passengers.add(passenger3);
        TempDrive tempDrive = new TempDrive(startDate, 600, 3, new HashSet<>(), passengers, new Route(), new Type());

        when(tempDriveRepository.findTempDriveById(1)).thenReturn(tempDrive);
        when(tempDriveRepository.save(Mockito.any(TempDrive.class))).thenAnswer(i -> i.getArguments()[0]);

        TempDrive actual = driveService.acceptDriveConsent(1);

        int expected = tempDrive.getPassengers().size() - (tempDrive.getPassengers().size() - 1);

        assertEquals(expected, actual.getNumAcceptedPassengers());
        verify(tempDriveRepository, times(1)).findTempDriveById(1);
        verify(tempDriveRepository, times(1)).save(Mockito.any(TempDrive.class));
    }

    @Test
    @DisplayName("Should return TempDrive with everyone accepting drive consent")
    public void testShouldReturnTempDriveWithEveryoneAccepting() {
        Timestamp startDate = new Timestamp(new Date().getTime());
        Passenger passenger1 = new Passenger();
        passenger1.setTokens(200);
        Passenger passenger2 = new Passenger();
        passenger2.setTokens(300);
        Passenger passenger3 = new Passenger();
        passenger3.setTokens(100);
        Set<Passenger> passengers = new HashSet<>();
        passengers.add(passenger1);
        passengers.add(passenger2);
        passengers.add(passenger3);
        TempDrive tempDrive = new TempDrive(startDate, 600, 3, new HashSet<>(), passengers, new Route(), new Type());
        tempDrive.addAcceptedPassenger();
        tempDrive.addAcceptedPassenger();

        when(tempDriveRepository.findTempDriveById(1)).thenReturn(tempDrive);
        when(driverService.selectDriverForDrive(tempDrive)).thenReturn(new Driver());
        when(driveRepository.save(Mockito.any(Drive.class))).thenAnswer(i -> i.getArguments()[0]);
        when(tempDriveRepository.save(Mockito.any(TempDrive.class))).thenAnswer(i -> i.getArguments()[0]);

        TempDrive actual = driveService.acceptDriveConsent(1);

        assertEquals(tempDrive.getPassengers().size(), actual.getNumAcceptedPassengers());
        verify(tempDriveRepository, times(1)).findTempDriveById(1);
        verify(tempDriveRepository, times(1)).save(Mockito.any(TempDrive.class));
        verify(driverService, times(1)).selectDriverForDrive(tempDrive);
    }

    @Test
    @DisplayName("Should return TempDrive with cancelled status upon rejection of consent")
    public void testShouldReturnTempDriveWithCancelledStatus() throws MessagingException {
        Timestamp startDate = new Timestamp(new Date().getTime());
        Route route = new Route();
        route.setRouteName("KISACKA 44, NOVI SAD, NOVI SAD-PUSKINOVA 27, NOVI SAD, NOVI SAD");
        Passenger rejectPassenger = new Passenger();
        rejectPassenger.setId(1);
        rejectPassenger.setName("Mika");
        rejectPassenger.setSurname("Mikic");
        Passenger passenger1 = new Passenger();
        passenger1.setId(2);
        passenger1.setName("Nika");
        passenger1.setSurname("Nikic");
        Passenger passenger2 = new Passenger();
        passenger2.setId(3);
        passenger2.setName("Kika");
        passenger2.setSurname("Kikic");
        Set<Passenger> passengers = new HashSet<>();
        passengers.add(rejectPassenger);
        passengers.add(passenger1);
        passengers.add(passenger2);

        TempDrive tempDrive = new TempDrive();
        tempDrive.setStartDate(startDate);
        tempDrive.setRoute(route);
        tempDrive.setPassengers(passengers);

        when(tempDriveRepository.save(Mockito.any(TempDrive.class))).thenAnswer(i -> i.getArguments()[0]);

        TempDrive actual = driveService.rejectDrive(tempDrive, rejectPassenger.getId(), rejectPassenger);

        assertEquals(Status.CANCELLED, actual.getStatus());
        verify(tempDriveRepository, times(1)).save(Mockito.any(TempDrive.class));
        verify(emailService, times(1)).sendDriveRejectedEmail(tempDrive, passenger1, rejectPassenger);
        verify(emailService, times(1)).sendDriveRejectedEmail(tempDrive, passenger2, rejectPassenger);
    }

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

    private Timestamp createPastTime() {
        Calendar calendar = Calendar.getInstance();
        int currentMinutes = calendar.get(Calendar.MINUTE);
        calendar.set(Calendar.MINUTE, currentMinutes - 1);
        return new Timestamp(calendar.getTime().getTime());
    }

    private Timestamp createMoreThanFiveHourTime() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinutes = calendar.get(Calendar.MINUTE);
        calendar.set(Calendar.HOUR_OF_DAY, currentHour + 5);
        calendar.set(Calendar.MINUTE, currentMinutes + 1);
        return new Timestamp(calendar.getTime().getTime());
    }
    private Timestamp createTimeJustBeforeReservationTime() {
        Calendar calendar = Calendar.getInstance();
        int currentMinutes = calendar.get(Calendar.MINUTE);
        calendar.set(Calendar.MINUTE, currentMinutes + 19);
        return new Timestamp(calendar.getTime().getTime());
    }

    private Timestamp createTimeJustInTimeForReservationTime() {
        Calendar calendar = Calendar.getInstance();
        int currentMinutes = calendar.get(Calendar.MINUTE);
        calendar.set(Calendar.MINUTE, currentMinutes + 20);
        return new Timestamp(calendar.getTime().getTime());
    }

    private Timestamp createCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        return new Timestamp(calendar.getTime().getTime());
    }

    private Timestamp createTimeFiveHoursAhead() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, currentHour + 5);
        return new Timestamp(calendar.getTime().getTime());
    }
}
