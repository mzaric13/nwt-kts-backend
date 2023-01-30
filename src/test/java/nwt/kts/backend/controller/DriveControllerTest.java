package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.TempDriveDTO;
import nwt.kts.backend.dto.login.EmailPasswordLoginDTO;
import nwt.kts.backend.dto.returnDTO.RouteDTO;
import nwt.kts.backend.dto.returnDTO.TokenDTO;
import nwt.kts.backend.dto.returnDTO.TypeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class DriveControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String accessTokenPassenger;

    private String accessTokenDriver;

    @BeforeEach
    public void loginDriver() {
        ResponseEntity<TokenDTO> responseEntity =
                restTemplate.postForEntity("/auth/login-credentials",
                        new EmailPasswordLoginDTO("mirko.ivanic@gmail.com", "sifra123"),
                        TokenDTO.class);
        TokenDTO tokenDTO = responseEntity.getBody();
        accessTokenDriver = tokenDTO.getAccessToken();
    }

    @BeforeEach
    public void loginPassenger() {
        ResponseEntity<TokenDTO> responseEntity =
                restTemplate.postForEntity("/auth/login-credentials",
                        new EmailPasswordLoginDTO("darko.darkovic@gmail.com", "sifra123"),
                        TokenDTO.class);
        TokenDTO tokenDTO = responseEntity.getBody();
        accessTokenPassenger = tokenDTO.getAccessToken();
    }

    @Test
    public void basicTest() {
        System.out.println(accessTokenPassenger);
        System.out.println(accessTokenDriver);
    }

    @Test
    @DisplayName("Should return a tempDriveDTO with an ID")
    public void testShouldReturnTempDriveDTO() {
        TempDriveDTO tempDriveDTO = new TempDriveDTO();
        tempDriveDTO.setEmails(new HashSet<>());
        tempDriveDTO.setTags(new HashSet<>());
        tempDriveDTO.getEmails().add("darko.darkovic@gmail.com");
        tempDriveDTO.setStartDate(createTimeJustInTimeForReservationTime());
        tempDriveDTO.setTypeDTO(new TypeDTO(1, "SUV"));
        RouteDTO routeDTO = new RouteDTO();
        tempDriveDTO.setRouteDTO(routeDTO);
        tempDriveDTO.getRouteDTO().setWaypoints(new ArrayList<>());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessTokenPassenger);
        HttpEntity<TempDriveDTO> entity = new HttpEntity<>(tempDriveDTO, headers);

        ResponseEntity<TempDriveDTO> responseEntity = restTemplate.postForEntity("/drives/create-temp-drive", entity, TempDriveDTO.class);
        TempDriveDTO outputTempDriveDTO = responseEntity.getBody();
        assertEquals(2, outputTempDriveDTO.getId());
    }

    @Test
    @DisplayName("Should return a 500 code for an empty passenger email list")
    public void testShouldReturnNonExistingEntityExceptionForEmptyPassengerList() {
        TempDriveDTO tempDriveDTO = new TempDriveDTO();
        tempDriveDTO.setEmails(new HashSet<>());
        tempDriveDTO.setTags(new HashSet<>());
        tempDriveDTO.setStartDate(createTimeJustInTimeForReservationTime());
        tempDriveDTO.setTypeDTO(new TypeDTO(1, "SUV"));
        RouteDTO routeDTO = new RouteDTO();
        tempDriveDTO.setRouteDTO(routeDTO);
        tempDriveDTO.getRouteDTO().setWaypoints(new ArrayList<>());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessTokenPassenger);
        HttpEntity<TempDriveDTO> entity = new HttpEntity<>(tempDriveDTO, headers);

        ResponseEntity<TempDriveDTO> responseEntity = restTemplate.postForEntity(
                "/drives/create-temp-drive", entity, TempDriveDTO.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Should return a 500 code for a passenger email that doesn't exist")
    public void testShouldReturnNonExistingEntityExceptionForNonExistingEmail() {
        TempDriveDTO tempDriveDTO = new TempDriveDTO();
        tempDriveDTO.setEmails(new HashSet<>());
        tempDriveDTO.getEmails().add("ne.postojim@gmail.com");
        tempDriveDTO.setTags(new HashSet<>());
        tempDriveDTO.setStartDate(createTimeJustInTimeForReservationTime());
        tempDriveDTO.setTypeDTO(new TypeDTO(1, "SUV"));
        RouteDTO routeDTO = new RouteDTO();
        tempDriveDTO.setRouteDTO(routeDTO);
        tempDriveDTO.getRouteDTO().setWaypoints(new ArrayList<>());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessTokenPassenger);
        HttpEntity<TempDriveDTO> entity = new HttpEntity<>(tempDriveDTO, headers);

        ResponseEntity<TempDriveDTO> responseEntity = restTemplate.postForEntity(
                "/drives/create-temp-drive", entity, TempDriveDTO.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Should return a 500 code for start time being in the past")
    public void testShouldReturnInvalidStartTimeExceptionForPastTime() {
        TempDriveDTO tempDriveDTO = new TempDriveDTO();
        tempDriveDTO.setEmails(new HashSet<>());
        tempDriveDTO.getEmails().add("darko.darkovic@gmail.com");
        tempDriveDTO.setTags(new HashSet<>());
        tempDriveDTO.setStartDate(createPastTime());
        tempDriveDTO.setTypeDTO(new TypeDTO(1, "SUV"));
        RouteDTO routeDTO = new RouteDTO();
        tempDriveDTO.setRouteDTO(routeDTO);
        tempDriveDTO.getRouteDTO().setWaypoints(new ArrayList<>());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessTokenPassenger);
        HttpEntity<TempDriveDTO> entity = new HttpEntity<>(tempDriveDTO, headers);

        ResponseEntity<TempDriveDTO> responseEntity = restTemplate.postForEntity(
                "/drives/create-temp-drive", entity, TempDriveDTO.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Should return a 500 code because start time is more than 5 hours ahead")
    public void testShouldReturnInvalidStartTimeExceptionForFutureTime() {
        TempDriveDTO tempDriveDTO = new TempDriveDTO();
        tempDriveDTO.setEmails(new HashSet<>());
        tempDriveDTO.getEmails().add("darko.darkovic@gmail.com");
        tempDriveDTO.setTags(new HashSet<>());
        tempDriveDTO.setStartDate(createMoreThanFiveHourTime());
        tempDriveDTO.setTypeDTO(new TypeDTO(1, "SUV"));
        RouteDTO routeDTO = new RouteDTO();
        tempDriveDTO.setRouteDTO(routeDTO);
        tempDriveDTO.getRouteDTO().setWaypoints(new ArrayList<>());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessTokenPassenger);
        HttpEntity<TempDriveDTO> entity = new HttpEntity<>(tempDriveDTO, headers);

        ResponseEntity<TempDriveDTO> responseEntity = restTemplate.postForEntity(
                "/drives/create-temp-drive", entity, TempDriveDTO.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Should return status code OK")
    public void testAcceptDriveConsent() {
        ResponseEntity<Void> responseEntity = restTemplate.exchange(
                "/drives/accept-drive-consent?tempDriveId={tempDriveId}", HttpMethod.PUT, null, Void.class, 1);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Should return status code OK")
    public void testRejectDriveConsent() {
        ResponseEntity<Void> responseEntity = restTemplate.exchange(
                "/drives/reject-drive-consent?tempDriveId={tempDriveId}&passengerId={passengerId}", HttpMethod.PUT,
                null, Void.class, 1, 1);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
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
