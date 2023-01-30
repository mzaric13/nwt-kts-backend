package service;

import nwt.kts.backend.dto.creation.TempDriveDTO;
import nwt.kts.backend.dto.returnDTO.DeclineDriveReasonDTO;
import nwt.kts.backend.dto.returnDTO.DriveDTO;
import nwt.kts.backend.dto.returnDTO.RouteDTO;
import nwt.kts.backend.dto.returnDTO.TypeDTO;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.exceptions.*;
import nwt.kts.backend.repository.DriveRepository;
import nwt.kts.backend.dto.returnDTO.MessageDTO;
import nwt.kts.backend.dto.returnDTO.NotificationDTO;
import nwt.kts.backend.dto.returnDTO.PassengerDTO;
import nwt.kts.backend.exceptions.NonExistingEntityException;
import nwt.kts.backend.repository.TempDriveRepository;
import nwt.kts.backend.service.*;
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

import org.springframework.messaging.simp.SimpMessagingTemplate;

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
    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;
    @Mock
    private ChatService chatService;

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


    /**
     * startNewDrive()
     */

    @Test
    @DisplayName("Start a new drive - throws exception, because there is not a new drive")
    public void startNewDriveThrowsNonExistingEntityException() {

        /* Objects */
        Driver driver = new Driver();
        driver.setId(1);

        /* Mocks */
        when(driveRepository.findFirstByDriverAndStatusOrderByIdDesc(driver, Status.PAID)).thenThrow(NonExistingEntityException.class);

        /* Asserts */
        assertThrows(NonExistingEntityException.class, () -> driveService.startNewDrive(driver));

    }

    @Test
    @DisplayName("Start a new drive - returns a drive object with Status.DRIVING_TO_START and hasFutureDrive = false")
    public void startNewDriveReturnDrive() {

        /* Objects */
        Driver driver = new Driver();
        driver.setId(2);
        driver.setHasFutureDrive(true);

        Drive newDrive = new Drive();
        newDrive.setDriver(driver);
        newDrive.setStatus(Status.STARTED);

        Drive savedDrive = new Drive();
        savedDrive.setDriver(driver);
        savedDrive.setStatus(Status.DRIVING_TO_START);


        /* Mocks */
        when(driveRepository.findFirstByDriverAndStatusOrderByIdDesc(driver, Status.PAID)).thenReturn(Optional.of(newDrive));
        when(driveRepository.save(newDrive)).thenReturn(savedDrive);

        /* Asserts */
        Drive drive = driveService.startNewDrive(driver);
        assertEquals(drive.getStatus(), Status.DRIVING_TO_START);
        assertFalse(drive.getDriver().isHasFutureDrive());
    }


    /**
     * reportInconsistency()
     */

    @Test
    @DisplayName("Report inconsistency - throws exception, because there is not a drive with given id")
    public void reportInconsistencyThrowsNonExistingException() {

        /* Objects */
        String email = "email@gmail.com";
        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1);

        /* Mocks */
        when(driveRepository.findDriveById(driveDTO.getId())).thenReturn(null);

        /* Asserts */
        assertThrows(NonExistingEntityException.class, () -> driveService.reportInconsistency(email, driveDTO));
    }

    @Test
    @DisplayName("Report inconsistency - returns a drive object with inconsistencies")
    public void reportInconsistencyReturnDrive() {

        /* Objects */
        String email = "email@gmail.com";

        ArrayList<String> inconsistentDriveReasonings = new ArrayList<>();
        inconsistentDriveReasonings.add("Wasn't going the route he said he was going to go.");
        inconsistentDriveReasonings.add("Drove much slower than he should of.");
        inconsistentDriveReasonings.add("Stopped at the store to buy something for himself.");

        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1);
        driveDTO.setInconsistentDriveReasoning(inconsistentDriveReasonings);

        String content = driveDTO.getInconsistentDriveReasoning().get(driveDTO.getInconsistentDriveReasoning().size() - 1);
        String chatName = email + "&" + "admin";

        Chat chat = new Chat();
        chat.setId(1);
        chat.setChatName("Chat name");

        Drive drive = new Drive();
        drive.setInconsistentDriveReasoning(new ArrayList<>());

        Message message = new Message();
        message.setId(1);
        message.setMessage("Wasn't going the route he said he was going to go.");
        message.setChat(chat);
        message.setSender("Driver");
        message.setTimestamp(new Timestamp(System.currentTimeMillis()));

        MessageDTO messageDTO = new MessageDTO(message);

        /* Mocks */
        when(driveRepository.findDriveById(driveDTO.getId())).thenReturn(drive);
        when(chatService.getChat(chatName)).thenReturn(chat);
        when(chatService.createMessage(content, chat, email)).thenReturn(message);
        doNothing().when(simpMessagingTemplate).convertAndSend("/secured/topic/messages/" + chatName, messageDTO);

        /* Asserts */
        Drive updatedDrive = driveService.reportInconsistency(email, driveDTO);

        assertEquals(updatedDrive.getInconsistentDriveReasoning().size(), 3);
        assertEquals(updatedDrive.getInconsistentDriveReasoning().get(0), "Wasn't going the route he said he was going to go.");

        verify(chatService, times(1)).getChat(chatName);
        verify(chatService, times(1)).createMessage(content, chat, email);
        verify(simpMessagingTemplate, times(1)).convertAndSend("/secured/topic/messages/" + chatName, messageDTO);
    }


    /**
     * sendNotificationsForReservedDrives()
     */

    @Test
    @DisplayName("Send notifications for reserved drives - doesn't send it because minute difference isn't 15, 10 or 5 minutes")
    public void sendNotificationsForReservedDrivesDoesntSendNotification() throws MessagingException {

        /* Objects */
        ArrayList<TempDrive> tempDrives = new ArrayList<>();

        // Granicni slucajevi
        ArrayList<Integer> minutes = new ArrayList<>();
        minutes.add(4);
        minutes.add(6);
        minutes.add(9);
        minutes.add(11);
        minutes.add(14);
        minutes.add(16);

        PassengerDTO passengerDTO = new PassengerDTO();

        NotificationDTO firstNotificationDTO = new NotificationDTO(1,
                "Your drive is starting in 15 minutes!\n" +
                        "There are 3 available drivers");

        NotificationDTO secondNotificationDTO = new NotificationDTO(1,
                "You and other passengers will receive an email where you will give your consent for the ride!" +
                        " Thank you for using our services!");

        for (int i = 0; i < 6; i ++) {
            TempDrive tempDrive = new TempDrive();
            tempDrive.setStartDate(createTimestampAfterGivenMinutes(minutes.get(i)));
            tempDrives.add(tempDrive);
        }

        /* Mocks */
        when(tempDriveRepository.findAllByStatus(Status.RESERVED)).thenReturn(tempDrives);
        doNothing().when(simpMessagingTemplate).convertAndSend("/secured/update/passengerStatus", passengerDTO);
        doNothing().when(simpMessagingTemplate).convertAndSend("/secured/update/updatePassenger", firstNotificationDTO);
        doNothing().when(simpMessagingTemplate).convertAndSend("/secured/update/updatePassenger", secondNotificationDTO);

        /* Asserts */
        driveService.sendNotificationsForReservedDrives();
        verify(simpMessagingTemplate, times(0)).convertAndSend("/secured/update/passengerStatus", passengerDTO);
        verify(simpMessagingTemplate, times(0)).convertAndSend("/secured/update/updatePassenger", firstNotificationDTO);
        verify(simpMessagingTemplate, times(0)).convertAndSend("/secured/update/updatePassenger", secondNotificationDTO);
    }

    @Test
    @DisplayName("Send notifications for reserved drives - minute difference is 15 minutes, sends it")
    public void sendNotificationsForReservedDrivesWhenDifferenceIs15Minutes() throws MessagingException {

        /* Objects */
        Set<Passenger> passengerSet = new HashSet<>();
        Role role = new Role();
        Passenger passenger = new Passenger("passenger@gmail.com", "0212323232", "sifra123", "Jovan", "Jovanovic",
                "Subotica", role, false, true, "pic.jpg", Provider.LOCAL);
        passenger.setHasDrive(false);
        passenger.setId(1);
        passengerSet.add(passenger);

        ArrayList<TempDrive> tempDrives = new ArrayList<>();
        TempDrive tempDrive = new TempDrive();
        tempDrive.setStartDate(createTimestampAfterGivenMinutes(15));
        tempDrives.add(tempDrive);
        tempDrive.setPassengers(passengerSet);

        PassengerDTO passengerDTO = new PassengerDTO(passenger);

        ArrayList<Driver> drivers = new ArrayList<>();
        Driver driver = new Driver();
        driver.setAvailable(true);
        drivers.add(driver);

        NotificationDTO firstNotificationDTO = new NotificationDTO(1,
                "Your drive is starting in 15 minutes!\n" +
                        "There are 1 available drivers");

        /* Mocks */
        when(tempDriveRepository.findAllByStatus(Status.RESERVED)).thenReturn(tempDrives);
        doNothing().when(simpMessagingTemplate).convertAndSend("/secured/update/passengerStatus", passengerDTO);
        when(passengerService.savePassenger(passenger)).thenReturn(passenger);
        when(driverService.getAllDrivers()).thenReturn(drivers);
        doNothing().when(simpMessagingTemplate).convertAndSend("/secured/update/updatePassenger", firstNotificationDTO);

        driveService.sendNotificationsForReservedDrives();
        verify(simpMessagingTemplate, times(1)).convertAndSend("/secured/update/passengerStatus", passengerDTO);
        verify(passengerService, times(1)).savePassenger(passenger);
        verify(simpMessagingTemplate, times(1)).convertAndSend("/secured/update/updatePassenger", firstNotificationDTO);
    }

    @Test
    @DisplayName("Send notifications for reserved drives - minute difference is 10 minutes, sends it")
    public void sendNotificationsForReservedDrivesWhenDifferenceIs10Minutes() throws MessagingException {

        /* Objects */
        Set<Passenger> passengerSet = new HashSet<>();
        Role role = new Role();
        Passenger passenger = new Passenger("passenger@gmail.com", "0212323232", "sifra123", "Jovan", "Jovanovic",
                "Subotica", role, false, true, "pic.jpg", Provider.LOCAL);
        passenger.setHasDrive(false);
        passenger.setId(1);
        passengerSet.add(passenger);

        ArrayList<TempDrive> tempDrives = new ArrayList<>();
        TempDrive tempDrive = new TempDrive();
        tempDrive.setStartDate(createTimestampAfterGivenMinutes(10));
        tempDrives.add(tempDrive);
        tempDrive.setPassengers(passengerSet);

        ArrayList<Driver> drivers = new ArrayList<>();
        Driver driver = new Driver();
        driver.setAvailable(true);
        drivers.add(driver);

        NotificationDTO firstNotificationDTO = new NotificationDTO(1,
                "Your drive is starting in 10 minutes!\n" +
                        "There are 1 available drivers");

        /* Mocks */
        when(tempDriveRepository.findAllByStatus(Status.RESERVED)).thenReturn(tempDrives);
        when(driverService.getAllDrivers()).thenReturn(drivers);
        doNothing().when(simpMessagingTemplate).convertAndSend("/secured/update/updatePassenger", firstNotificationDTO);

        /* Asserts */
        driveService.sendNotificationsForReservedDrives();
        verify(simpMessagingTemplate, times(1)).convertAndSend("/secured/update/updatePassenger", firstNotificationDTO);


    }

    @Test
    @DisplayName("Send notifications for reserved drives - minute difference is 5 minutes, sends it")
    public void sendNotificationsForReservedDrivesWhenDifferenceIs5Minutes() throws MessagingException {

        /* Objects */
        Set<Passenger> passengerSet = new HashSet<>();
        Role role = new Role();
        Passenger passenger = new Passenger("passenger@gmail.com", "0212323232", "sifra123", "Jovan", "Jovanovic",
                "Subotica", role, false, true, "pic.jpg", Provider.LOCAL);
        passenger.setHasDrive(false);
        passenger.setId(1);
        passengerSet.add(passenger);

        ArrayList<TempDrive> tempDrives = new ArrayList<>();
        TempDrive tempDrive = new TempDrive();
        tempDrive.setStartDate(createTimestampAfterGivenMinutes(5));
        tempDrives.add(tempDrive);
        tempDrive.setPassengers(passengerSet);

        NotificationDTO secondNotificationDTO = new NotificationDTO(1,
                "You and other passengers will receive an email where you will give your consent for the ride!" + " Thank you for using our services!");

        when(tempDriveRepository.findAllByStatus(Status.RESERVED)).thenReturn(tempDrives);
        doNothing().when(simpMessagingTemplate).convertAndSend("/secured/update/updatePassenger", secondNotificationDTO);
        doNothing().when(emailService).sendDriveConfirmationEmail(tempDrive, passenger);

        /* Asserts */
        driveService.sendNotificationsForReservedDrives();
        verify(simpMessagingTemplate, times(1)).convertAndSend("/secured/update/updatePassenger", secondNotificationDTO);
        verify(emailService, times(1)).sendDriveConfirmationEmail(tempDrive, passenger);
    }

    private Timestamp createTimestampAfterGivenMinutes(int minutes) {
        Calendar calendar = Calendar.getInstance();
        int currentMinutes = calendar.get(Calendar.MINUTE);
        calendar.set(Calendar.MINUTE, currentMinutes + minutes);
        return new Timestamp(calendar.getTime().getTime());
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
