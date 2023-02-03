package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.TempDriveDTO;
import nwt.kts.backend.dto.returnDTO.*;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.exceptions.DriverNotFoundException;
import nwt.kts.backend.exceptions.NotEnoughTokensException;
import nwt.kts.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping(value="/drives")
public class DriveController {

    @Autowired
    private DriveService driveService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/get-drives")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDrives(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Page<Drive> drivePage = driveService.getDrives(page, size);
        return new ResponseEntity<>(createDrivesResponse(drivePage), HttpStatus.OK);
    }

    @GetMapping("/get-drives-for-driver")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, Object>> getDrivesForDriver(Principal principal, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Driver driver = driverService.findDriverByEmail(principal.getName());
        Page<Drive> drivePage = driveService.getDrivesByDriver(page, size, driver);
        return new ResponseEntity<>(createDrivesResponse(drivePage), HttpStatus.OK);
    }

    @GetMapping("/get-drives-for-passenger")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<Map<String, Object>> getDrivesForPassenger(Principal principal, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Passenger passenger = passengerService.findPassengerByEmail(principal.getName());
        Page<Drive> drivePage = driveService.getDrivesByPassenger(page, size, passenger);
        return new ResponseEntity<>(createDrivesResponse(drivePage), HttpStatus.OK);
    }

    @PostMapping("/create-temp-drive")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<TempDriveDTO> createTempDrive(@RequestBody TempDriveDTO tempDriveDTO) {
        TempDrive tempDrive = driveService.saveTempDrive(tempDriveDTO);
        return new ResponseEntity<>(new TempDriveDTO(tempDrive), HttpStatus.CREATED);
    }

    @GetMapping("/send-confirmation-email/{tempDriveId}")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<Void> sendConfirmationEmail(@PathVariable Integer tempDriveId) throws MessagingException {
        TempDrive tempDrive = driveService.getTempDriveById(tempDriveId);
        driveService.sendConfirmationEmail(tempDrive);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/accept-drive-consent")
    public ResponseEntity<Void> acceptDriveConsent(@RequestParam("tempDriveId") Integer tempDriveId,
                                                   @RequestParam("passengerId") Integer passengerId) {
        TempDrive tempDrive = driveService.acceptDriveConsent(tempDriveId, passengerId);
        if (tempDrive.getDriveId() != null) {
            Drive drive = driveService.getDriveById(tempDrive.getDriveId());
            if (drive.getStatus() == Status.DRIVING_TO_START) {
                simpMessagingTemplate.convertAndSend("/secured/update/newDrive", new DriveDTO(drive));
                simpMessagingTemplate.convertAndSend("/secured/update/driverStatus", new DriverDTO(drive.getDriver()));
            }
        } else {
            tempDrive = driveService.setPassengersFalseHasDrive(tempDrive);
            tempDrive.getPassengers().forEach(passenger -> {
                simpMessagingTemplate.convertAndSend("/secured/update/passengerStatus", new PassengerDTO(passenger));
            });
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/reject-drive-consent")
    public ResponseEntity<Void> rejectDriveConsent(@RequestParam("tempDriveId") Integer tempDriveId,
                                                   @RequestParam("passengerId") Integer passengerId) throws MessagingException {
        TempDrive tempDrive = driveService.getTempDriveById(tempDriveId);
        Passenger rejectPassenger = passengerService.findPassengerById(passengerId);
        driveService.rejectDrive(tempDrive, passengerId, rejectPassenger);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/get-accepted-drive", consumes = "application/json", produces = "application/json")
    public ResponseEntity<DriveDTO> getPaidDriveForDriver(@RequestBody DriverDTO driverDTO) {
        Driver driver = driverService.findDriverById(driverDTO.getId());
        Drive drive = driveService.getDriveForDriverByStatus(driver, Status.DRIVING_TO_START);
        return new ResponseEntity<>(new DriveDTO(drive), HttpStatus.OK);
    }

    @GetMapping(value = "/get-started-drive", consumes = "application/json", produces = "application/json")
    public ResponseEntity<DriveDTO> getStartedDriveForDriver(@RequestBody DriverDTO driverDTO) {
        Driver driver = driverService.findDriverById(driverDTO.getId());
        Drive drive = driveService.getDriveForDriverByStatus(driver, Status.STARTED);
        return new ResponseEntity<>(new DriveDTO(drive), HttpStatus.OK);
    }

    @GetMapping(value = "/get-ended-drive", consumes = "application/json", produces = "application/json")
    public ResponseEntity<DriveDTO> getEndedDriveForDriver(@RequestBody DriverDTO driverDTO) {
        Driver driver = driverService.findDriverById(driverDTO.getId());
        Drive drive = driveService.getDriveForDriverByStatus(driver, Status.FINISHED);
        return new ResponseEntity<>(new DriveDTO(drive), HttpStatus.OK);
    }

    @PutMapping(value = "/start-drive", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriveDTO> startDrive(@RequestBody DriveDTO driveDTO) {
        Drive drive = driveService.startDrive(driveDTO);
        simpMessagingTemplate.convertAndSend("/secured/update/driveStatus", new DriveDTO(drive));
        return new ResponseEntity<>(new DriveDTO(drive), HttpStatus.OK);
    }

    @PutMapping(value = "/end-drive", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriveDTO> endDrive(@RequestBody DriveDTO driveDTO) {
        Drive drive = driveService.endDrive(driveDTO);
        DriveDTO returnDrive = new DriveDTO(drive);
        simpMessagingTemplate.convertAndSend("/secured/update/end-drive", returnDrive);
        if (!drive.getDriver().isAvailable()) {
            Drive newDrive = driveService.startNewDrive(drive.getDriver());
            simpMessagingTemplate.convertAndSend("/secured/update/newDrive", new DriveDTO(newDrive));
        } else {
            simpMessagingTemplate.convertAndSend("/secured/update/driverStatus", new DriverDTO(drive.getDriver()));
        }
        return new ResponseEntity<>(returnDrive, HttpStatus.OK);
    }

    @GetMapping(value = "/{driveId}", produces = "application/json")
    @PreAuthorize("hasAnyRole('DRIVER', 'PASSENGER', 'ADMIN')")
    public ResponseEntity<DriveDTO> getDrive(@PathVariable Integer driveId) {
        Drive drive = driveService.getDriveById(driveId);
        return new ResponseEntity<>(new DriveDTO(drive), HttpStatus.OK);
    }

    @PutMapping(value = "/report-inconsistency", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<Void> reportInconsistency(Principal principal, @RequestBody DriveDTO driveDTO) {
        Drive drive = driveService.reportInconsistency(principal.getName(), driveDTO);
        simpMessagingTemplate.convertAndSend("/secured/update/drive-inconsistency", new DriveDTO(drive));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Map<String, Object> createDrivesResponse(Page<Drive> drivePage) {
        Map<String, Object> returnValue = new HashMap<>();
        List<DriveDTO> driveDTOS = new ArrayList<>();
        for (Drive drive: drivePage.getContent()) {
            DriverDTO driverDTO = createDriverDTO(drive);
            List<PassengerDTO> passengerDTOS = createPassengersListDTO(drive);
            driveDTOS.add(new DriveDTO(drive, driverDTO, passengerDTOS));
        }
        returnValue.put("drives", driveDTOS);
        returnValue.put("totalItems", drivePage.getTotalElements());
        returnValue.put("totalPages", drivePage.getTotalPages());
        return returnValue;
    }

    private DriverDTO createDriverDTO(Drive drive) {
        DriverDTO driverDTO;
        ImageDataDTO driverImageDTO = driverService.getImageDataForDriver(drive.getDriver());
        if (driverImageDTO != null) {
            driverDTO = new DriverDTO(drive.getDriver(), driverImageDTO);
        }else {
            driverDTO = new DriverDTO(drive.getDriver());
        }
        return driverDTO;
    }

    private List<PassengerDTO> createPassengersListDTO(Drive drive) {
        List<PassengerDTO> passengerDTOS = new ArrayList<>();
        for (Passenger passenger: drive.getPassengers()) {
            ImageDataDTO passengerImageData = passengerService.createImageDataForPassenger(passenger);
            PassengerDTO passengerDTO;
            if (passengerImageData != null) {
                passengerDTO = new PassengerDTO(passenger, passengerImageData);
            }
            else {
                passengerDTO = new PassengerDTO(passenger);
            }
            passengerDTOS.add(passengerDTO);
        }
        return passengerDTOS;
    }

    @PutMapping(value = "/accept-drive", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriveDTO> acceptDrive(@RequestBody DriveDTO driveDTO, Principal principal) {
        Driver driver = driverService.findDriverByEmail(principal.getName());
        Drive drive = driveService.driverAcceptDrive(driver, driveDTO);
        simpMessagingTemplate.convertAndSend("/secured/update/driverStatus", new DriverDTO(drive.getDriver()));
        return new ResponseEntity<>(new DriveDTO(drive), HttpStatus.OK);
    }

    @PutMapping(value = "/decline-drive", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriveDTO> declineDrive(@RequestBody DeclineDriveReasonDTO declineDriveReasonDTO, Principal principal) throws MessagingException {
        Driver driver = driverService.findDriverByEmail(principal.getName());
        Drive drive = driveService.driverDeclineDrive(driver, declineDriveReasonDTO);
        simpMessagingTemplate.convertAndSend("/secured/update/driveStatus", new DriveDTO(drive));
        simpMessagingTemplate.convertAndSend("/secured/update/driverStatus", new DriverDTO(drive.getDriver()));
        if (!drive.getDriver().isAvailable()) {
            drive = driveService.getDriveForDriverByStatus(drive.getDriver(), Status.PAID);
            simpMessagingTemplate.convertAndSend("/secured/update/newDrive", new DriveDTO(drive));
        }
        return new ResponseEntity<>(new DriveDTO(drive), HttpStatus.OK);
    }

    @GetMapping(value = "/get-rejected-drive", consumes = "application/json", produces = "application/json")
    public ResponseEntity<DriveDTO> getRejectedDrive(@RequestBody DriverDTO driverDTO) {
        Drive drive = driveService.getRejectedDrive(driverDTO);
        return new ResponseEntity<>(new DriveDTO(drive), HttpStatus.OK);
    }
}
