package nwt.kts.backend.service;

import nwt.kts.backend.dto.returnDTO.DriveDTO;
import nwt.kts.backend.dto.returnDTO.MessageDTO;
import nwt.kts.backend.dto.returnDTO.NotificationDTO;
import nwt.kts.backend.dto.returnDTO.PassengerDTO;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.exceptions.NonExistingEntityException;
import nwt.kts.backend.repository.DriveRepository;
import nwt.kts.backend.repository.PassengerRepository;
import nwt.kts.backend.repository.TempDriveRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.util.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DriveServiceTest {

    @InjectMocks
    private DriveService driveService;

    @Mock
    private DriveRepository driveRepository;

    @Mock
    private PassengerService passengerService;

    @Mock
    private ChatService chatService;

    @Mock
    private DriverService driverService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private TempDriveRepository tempDriveRepository;

    @Mock
    private EmailService emailService;


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
                "Your drive is starting in 5 minutes!\n" +
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
}
