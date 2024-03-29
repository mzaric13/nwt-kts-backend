package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.TempDriveDTO;
import nwt.kts.backend.dto.returnDTO.*;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.exceptions.*;
import nwt.kts.backend.repository.DriveRepository;
import nwt.kts.backend.repository.TempDriveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DriveService {

    @Autowired
    private DriveRepository driveRepository;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private TempDriveRepository tempDriveRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public Drive saveDrive(Drive drive) {
        return driveRepository.save(drive);
    }

    public Drive getDriveById(int driveId) {
        return driveRepository.findDriveById(driveId);
    }

    public Page<Drive> getDrives(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return driveRepository.findAllByStatusOrderByStartDateAsc(Status.FINISHED, pageable);
    }

    public Page<Drive> getDrivesByDriver(int page, int size, Driver driver) {
        Pageable pageable = PageRequest.of(page, size);
        return driveRepository.findAllByDriverAndStatusOrderByStartDateAsc(driver, Status.FINISHED, pageable);
    }

    public Page<Drive> getDrivesByPassenger(int page, int size, Passenger passenger) {
        Pageable pageable = PageRequest.of(page, size);
        return driveRepository.findAllByPassengersContainsAndStatusOrderByStartDateAsc(passenger, Status.FINISHED, pageable);
    }

    public TempDrive saveTempDrive(TempDriveDTO tempDriveDTO) {
        if (!passengerService.allPassengersExist(tempDriveDTO.getEmails()))
            throw new NonExistingEntityException("Not all passenger emails exist");
        Set<Passenger> passengers = tempDriveDTO.getEmails().stream()
                .map(email -> passengerService.findPassengerByEmail(email)).collect(Collectors.toSet());
        for (Passenger passenger: passengers) {
            if (passenger.getHasDrive()) throw new PassengerHasDriveException("One of selected passenger is currently on another drive");
        }
        checkIfTimeValid(tempDriveDTO.getStartDate());
        Type type = typeService.findTypeByName(tempDriveDTO.getTypeDTO().getName());
        TempDrive tempDrive = new TempDrive(tempDriveDTO, passengers, type);
        if (checkIfIsForReservation(tempDrive.getStartDate())) {
            tempDrive.setStatus(Status.RESERVED);
        } else {
            tempDrive.setStatus(Status.PENDING);
        }
        Route route = routeService.saveRoute(tempDrive.getRoute());
        tempDrive.setRoute(route);
        return tempDriveRepository.save(tempDrive);
    }

    public TempDrive acceptDriveConsent(Integer tempDriveId, Integer passengerId) {
        TempDrive tempDrive = tempDriveRepository.findTempDriveById(tempDriveId);
        this.acceptDrive(tempDrive, passengerId);
        if (this.allPassengersAcceptedDrive(tempDrive)) {
            if (this.passengersHaveTokens(tempDrive)) {
                Driver driver = driverService.selectDriverForDrive(tempDrive);
                if (driver == null) throw new DriverNotFoundException("There are no available drivers right now!");
                this.payDrive(tempDrive);
                Drive drive = this.createDrive(tempDrive, driver);
                tempDrive.setDriveId(drive.getId());
            } else {
                throw new NotEnoughTokensException("You don't have enough tokens to pay for the ride!");
            }
        }
        return tempDriveRepository.save(tempDrive);
    }

    public TempDrive getTempDriveById(int id) {
        return tempDriveRepository.findTempDriveById(id);
    }

    private boolean passengersHaveTokens(TempDrive tempDrive) {
        double costPerPerson = tempDrive.getPrice() / tempDrive.getPassengers().size();
        double residueCost = 0;
        List<Passenger> sortedPassengers = sortPassengersByTokenAmount(tempDrive.getPassengers());
        if (sortedPassengers.get(0).getTokens() >= tempDrive.getPrice()) return true;
        for (Passenger passenger : sortedPassengers) {
            double costPerPersonWithResidue = costPerPerson + residueCost;
            if (passenger.getTokens() >= costPerPersonWithResidue) {
                residueCost = 0;
            }
            else {
                residueCost = costPerPersonWithResidue - passenger.getTokens();
            }
        }
        return residueCost == 0;
    }

    private List<Passenger> sortPassengersByTokenAmount(Set<Passenger> passengers) {
        List<Passenger> sortedPassengers = new ArrayList<>(passengers);
        sortedPassengers.sort(Comparator.comparingDouble(Passenger::getTokens));
        return sortedPassengers;
    }

    public TempDrive rejectDrive(TempDrive tempDrive, Integer passengerId, Passenger rejectPassenger) throws MessagingException {
        if (!tempDrive.getAnsweredPassengers().contains(passengerId)) {
            tempDrive.getAnsweredPassengers().add(passengerId);
            for (Passenger passenger : tempDrive.getPassengers()) {
                if (!Objects.equals(passenger.getId(), passengerId)) {
                    emailService.sendDriveRejectedEmail(tempDrive, passenger, rejectPassenger);
                }
            }
            tempDrive.setStatus(Status.CANCELLED);
            return tempDriveRepository.save(tempDrive);
        }
        throw new PassengerAlreadyAnsweredException("You have already given/not given your consent for this drive!");
    }

    public void sendConfirmationEmail(TempDrive tempDrive) throws MessagingException {
        for (Passenger passenger : tempDrive.getPassengers()) {
            emailService.sendDriveConfirmationEmail(tempDrive, passenger);
        }
    }

    private void acceptDrive(TempDrive tempDrive, int passengerId) {
        if (!tempDrive.getAnsweredPassengers().contains(passengerId)) {
            tempDrive.addAcceptedPassenger();
            tempDrive.getAnsweredPassengers().add(passengerId);
        } else {
            throw new PassengerAlreadyAnsweredException("You have already given/not given your consent for this drive!");
        }
    }

    private boolean allPassengersAcceptedDrive(TempDrive tempDrive) {
        return tempDrive.getNumAcceptedPassengers() == tempDrive.getPassengers().size();
    }

    private Drive createDrive(TempDrive tempDrive, Driver driver) {
        Drive drive = new Drive(tempDrive, driver);
        if (driver.isAvailable()) {
            drive.setStatus(Status.DRIVING_TO_START);
            drive.getDriver().setAvailable(false);
        }
        for (Passenger passenger: drive.getPassengers()) passenger.setHasDrive(true);
        return driveRepository.save(drive);
    }

    private void payDrive(TempDrive tempDrive) {
        double costPerPerson = tempDrive.getPrice() / tempDrive.getPassengers().size();
        double residueCost = 0;
        List<Passenger> sortedPassengers = sortPassengersByTokenAmount(tempDrive.getPassengers());
        for (Passenger passenger : sortedPassengers) {
            double costPerPersonWithResidue = costPerPerson + residueCost;
            if (passenger.getTokens() >= costPerPersonWithResidue) {
                passenger.payDrive(costPerPersonWithResidue);
                residueCost = 0;
            }
            else {
                residueCost = costPerPersonWithResidue - passenger.getTokens();
                passenger.setTokens(0);
            }
        }
    }

    public Drive getDriveForDriverByStatus(Driver driver, Status status) {
        return driveRepository.findFirstByDriverAndStatusOrderByIdDesc(driver, status).orElseThrow(() -> {throw new NonExistingEntityException("No drive");});
    }

    public Drive startDrive(DriveDTO driveDTO) {
        Drive drive = driveRepository.findDriveById(driveDTO.getId());
        if (drive == null) throw new NonExistingEntityException("Drive is not found");
        if (!checkDriverPositionToDrive(drive.getDriver(), drive.getRoute().getWaypoints().get(0))) throw new DriverNotOnLocationException("You can't start drive without being on location.");
        drive.setStatus(Status.STARTED);
        return driveRepository.save(drive);
    }

    public Drive endDrive(DriveDTO driveDTO) {
        Drive drive = driveRepository.findDriveById(driveDTO.getId());
        if (drive == null) throw new NonExistingEntityException("Drive is not found");
        Driver driver = drive.getDriver();
        if (!checkDriverPositionToDrive(driver, drive.getRoute().getWaypoints().get(drive.getRoute().getWaypoints().size() - 1))) throw new DriverNotOnLocationException("You can't end drive without being on location.");
        if (!driver.isHasFutureDrive()) driver.setAvailable(true);
        else driver.setHasFutureDrive(false);
        for (Passenger pas : drive.getPassengers()) pas.setHasDrive(false);
        drive.setStatus(Status.FINISHED);
        drive.setEndDate(new Timestamp(new Date().getTime()));
        return driveRepository.save(drive);
    }

    public Drive startNewDrive(Driver driver) {
        Drive newDrive = driveRepository.findFirstByDriverAndStatusOrderByIdDesc(driver, Status.PAID).orElseThrow(() -> {throw new NonExistingEntityException("There is not new drive");});
        newDrive.setStatus(Status.DRIVING_TO_START);
        newDrive.getDriver().setHasFutureDrive(false);
        return driveRepository.save(newDrive);
    }

    private boolean checkDriverPositionToDrive(Driver driver, Point point) {
        return Math.abs(driver.getLocation().getLatitude() - point.getLatitude()) < 10e-4 && Math.abs(driver.getLocation().getLongitude() - point.getLongitude()) < 10e-4;
    }

    public Drive reportInconsistency(String email, DriveDTO driveDTO) {
        Drive drive = driveRepository.findDriveById(driveDTO.getId());
        if (drive == null) throw new NonExistingEntityException("Drive is not found");
        drive.setInconsistentDriveReasoning(driveDTO.getInconsistentDriveReasoning());
        String content = drive.getInconsistentDriveReasoning().get(drive.getInconsistentDriveReasoning().size() - 1);
        String chatName = email + "&" + "admin";
        Chat chat = chatService.getChat(chatName);
        Message message = chatService.createMessage(content, chat, email);
        simpMessagingTemplate.convertAndSend("/secured/topic/messages/" + chatName, new MessageDTO(message));
        return drive;
    }

    public Drive driverAcceptDrive(Driver driver, DriveDTO driveDTO) {
        Drive drive = driveRepository.findDriveById(driveDTO.getId());
        if (driver.isAvailable()) {
            driver.setAvailable(false);
            drive.setStatus(Status.DRIVING_TO_START);
            return driveRepository.save(drive);
        }
        return drive;
    }

    public Drive driverDeclineDrive(Driver driver, DeclineDriveReasonDTO declineDriveReasonDTO) throws MessagingException {
        Drive drive = driveRepository.findDriveById(declineDriveReasonDTO.getDriveDTO().getId());
        drive.setStatus(Status.CANCELLED);
        if (!driver.isHasFutureDrive()) {
            driver.setAvailable(true);
        } else {
            driver.setHasFutureDrive(false);
        }
        double tokensToReturn = drive.getPrice() / drive.getPassengers().size();
        for (Passenger passenger: drive.getPassengers()) {
            emailService.sendDriverRejectedDriveEmail(drive, declineDriveReasonDTO.getReasonForDeclining(), passenger);
            passenger.setHasDrive(false);
            passenger.setTokens(passenger.getTokens() + tokensToReturn);
        }
        return driveRepository.save(drive);
    }

    public Drive getRejectedDrive(DriverDTO driverDTO) {
        Driver driver = driverService.findDriverById(driverDTO.getId());
        Drive lasDrive = driveRepository.findFirstByDriverOrderByIdDesc(driver).orElseThrow(() -> {throw new NonExistingEntityException("No drive for driver");});
        Drive rejectedDrive = driveRepository.findFirstByDriverAndStatusOrderByIdDesc(driver, Status.CANCELLED).orElseThrow(() -> {throw new NonExistingEntityException("Drive is not cancelled.");});
        if (!lasDrive.getId().equals(rejectedDrive.getId())) throw new NonExistingEntityException("No rejected drive");
        return rejectedDrive;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional(readOnly = false)
    public void sendNotificationsForReservedDrives() throws MessagingException {
        List<TempDrive> reservedDrives = tempDriveRepository.findAllByStatus(Status.RESERVED);
        long currentMinutes = new Timestamp(new Date().getTime()).getTime() / 60000;
        for (TempDrive reservedDrive : reservedDrives) {
            long driveStartMinutes = reservedDrive.getStartDate().getTime() / 60000;
            long difference = driveStartMinutes - currentMinutes;
            if (difference == 15) {
                reservedDrive.getPassengers().forEach(passenger -> {
                    passenger.setHasDrive(true);
                    simpMessagingTemplate.convertAndSend("/secured/update/passengerStatus", new PassengerDTO(passenger));
                    passengerService.savePassenger(passenger);
                });
            }
            if (difference == 15 || difference == 10) {
                List<Driver> drivers = driverService.getAllDrivers();
                int numOfAvailableDrivers = (int) drivers.stream().filter(Driver::isAvailable).count();
                reservedDrive.getPassengers().forEach(passenger -> {
                    simpMessagingTemplate.convertAndSend("/secured/update/updatePassenger",
                            new NotificationDTO(passenger.getId(),"Your drive is starting in " + difference + " minutes!\n" +
                                    "There are " + numOfAvailableDrivers + " available drivers"));
                });
            } else if (difference == 5) {
                reservedDrive.getPassengers().forEach(passenger -> {
                    simpMessagingTemplate.convertAndSend("/secured/update/updatePassenger",
                            new NotificationDTO(passenger.getId(),"You and other passengers will receive an email where you will give your consent for the ride!" + " Thank you for using our services!"));
                });
                for (Passenger passenger : reservedDrive.getPassengers()) {
                    emailService.sendDriveConfirmationEmail(reservedDrive, passenger);
                }
            }
        }
    }

    private void checkIfTimeValid(Timestamp startTime) {
        Timestamp currentTime = new Timestamp(new Date().getTime());
        long startTimeInMinutes = startTime.getTime() / 60000;
        long currentTimeInMinutes = currentTime.getTime() / 60000;
        long difference = startTimeInMinutes - currentTimeInMinutes;
        if (difference < 0) {
            throw new InvalidStartTimeException("Can't order drive for the past!");
        } else if (difference > 300) {
            throw new InvalidStartTimeException("Can't order drive more than 5 hours in advance!");
        }
    }

    private boolean checkIfIsForReservation(Timestamp startDate) {
        Timestamp currentTime = new Timestamp(new Date().getTime());
        long startTimeInMinutes = startDate.getTime() / 60000;
        long currentTimeInMinutes = currentTime.getTime() / 60000;
        long difference = startTimeInMinutes - currentTimeInMinutes;
        return difference >= 20;
    }

    public TempDrive setPassengersFalseHasDrive(TempDrive tempDrive) {
        tempDrive.getPassengers().forEach(passenger -> passenger.setHasDrive(false));
        return tempDriveRepository.save(tempDrive);
    }
}
