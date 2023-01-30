package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.TempDriveDTO;
import nwt.kts.backend.dto.returnDTO.RouteDTO;
import nwt.kts.backend.dto.returnDTO.TypeDTO;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.exceptions.DriverNotFoundException;
import nwt.kts.backend.exceptions.InvalidStartTimeException;
import nwt.kts.backend.exceptions.NotEnoughTokensException;
import nwt.kts.backend.exceptions.PassengerHasDriveException;
import nwt.kts.backend.repository.DriveRepository;
import nwt.kts.backend.repository.TempDriveRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
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
