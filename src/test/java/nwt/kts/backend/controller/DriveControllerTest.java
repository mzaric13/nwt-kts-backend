package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.TempDriveDTO;
import nwt.kts.backend.dto.login.EmailPasswordLoginDTO;
import nwt.kts.backend.dto.returnDTO.*;
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

    @Test
    @DisplayName("Send emails - Should return status code OK")
    public void testSendEmailsReturnStatusCodeOK() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessTokenPassenger);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/drives/send-confirmation-email/{tempDriveId}", HttpMethod.GET, httpEntity, Void.class, 1);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Send emails - Should return status code INTERNAL_SERVER_ERROR because there isn't a Tempdrive with given ID")
    public void testSendEmailsReturnStatusInternalServerError() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessTokenPassenger);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/drives/send-confirmation-email/{tempDriveId}", HttpMethod.GET, httpEntity, Void.class, 1000);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Report inconsistency - Should return status code OK")
    public void testReportInconsistencyReturnStatusOK() {

        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1);

        ArrayList<String> inconsistentDriveReasonings = new ArrayList<>();
        inconsistentDriveReasonings.add("Wasn't going the route he said he was going to go.");
        inconsistentDriveReasonings.add("Drove much slower than he should of.");
        inconsistentDriveReasonings.add("Stopped at the store to buy something for himself.");

        driveDTO.setInconsistentDriveReasoning(inconsistentDriveReasonings);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessTokenPassenger);
        HttpEntity<DriveDTO> httpEntity = new HttpEntity<>(driveDTO, httpHeaders);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/drives/report-inconsistency", HttpMethod.PUT, httpEntity, Void.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Report inconsistency - Should return status code INTERNAL_SERVER_ERROR because there isn't a Drive with given ID")
    public void testReportInconsistencyReturnStatusInternalServerError() {

        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1000);

        ArrayList<String> inconsistentDriveReasonings = new ArrayList<>();
        inconsistentDriveReasonings.add("Wasn't going the route he said he was going to go.");
        inconsistentDriveReasonings.add("Drove much slower than he should of.");
        inconsistentDriveReasonings.add("Stopped at the store to buy something for himself.");

        driveDTO.setInconsistentDriveReasoning(inconsistentDriveReasonings);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessTokenPassenger);
        HttpEntity<DriveDTO> httpEntity = new HttpEntity<>(driveDTO, httpHeaders);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/drives/report-inconsistency", HttpMethod.PUT, httpEntity, Void.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Reject drive - Should return status code OK")
    public void testRejectDriveReturnStatusOK() {

        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1);

        DeclineDriveReasonDTO declineDriveReasonDTO = new DeclineDriveReasonDTO(driveDTO, "I feel sick");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessTokenDriver);
        HttpEntity<DeclineDriveReasonDTO> httpEntity = new HttpEntity<>(declineDriveReasonDTO, httpHeaders);

        ResponseEntity<DriveDTO> responseEntity = restTemplate.exchange("/drives/decline-drive", HttpMethod.PUT, httpEntity, DriveDTO.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, responseEntity.getBody().getId());
    }

    @Test
    @DisplayName("Reject drive - Should return status code INTERNAL_SERVER_ERROR because there isn't a Drive with given ID")
    public void testRejectDriveReturnStatusInternalServerErrorNoDriverWithGivenID() {

        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1000);
        DeclineDriveReasonDTO declineDriveReasonDTO = new DeclineDriveReasonDTO(driveDTO, "I feel sick");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessTokenDriver);
        HttpEntity<DeclineDriveReasonDTO> httpEntity = new HttpEntity<>(declineDriveReasonDTO, httpHeaders);

        ResponseEntity<DriveDTO> responseEntity = restTemplate.exchange("/drives/decline-drive", HttpMethod.PUT, httpEntity, DriveDTO.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Reject drive - Should return status code INTERNAL_SERVER_ERROR because there isn't a Drive with given ID")
    public void testRejectDriveReturnStatusInternalServerErrorNoPaidDriveForDriver() {

        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(1);
        DeclineDriveReasonDTO declineDriveReasonDTO = new DeclineDriveReasonDTO(driveDTO, "I feel sick");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessTokenDriver);
        HttpEntity<DeclineDriveReasonDTO> httpEntity = new HttpEntity<>(declineDriveReasonDTO, httpHeaders);
        HttpEntity<Object> httpEntityGet = new HttpEntity<>(httpHeaders);

        // Change driver availability to false, so that this case can be tested
        ResponseEntity<DriverDTO> responseEntityDriver = restTemplate.exchange("/drivers/change-status", HttpMethod.GET, httpEntityGet, DriverDTO.class);

        ResponseEntity<DriveDTO> responseEntity = restTemplate.exchange("/drives/decline-drive", HttpMethod.PUT, httpEntity, DriveDTO.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Start drive - Should return ok status")
    public void testStartDriveShouldReturnOkStatus() {
        ResponseEntity<TokenDTO> responseEntityTokenDTO =
                restTemplate.postForEntity("/auth/login-credentials",
                        new EmailPasswordLoginDTO("branko.lazic@gmail.com", "sifra123"),
                        TokenDTO.class);
        TokenDTO tokenDTO = responseEntityTokenDTO.getBody();
        String accessToken = tokenDTO.getAccessToken();

        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(2);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        HttpEntity<DriveDTO> httpEntity = new HttpEntity<>(driveDTO, httpHeaders);

        ResponseEntity<DriveDTO> responseEntity = restTemplate.exchange("/drives/start-drive", HttpMethod.PUT, httpEntity, DriveDTO.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Start drive - Should return INTERNAL_SERVER_ERROR status because drive with given id doesn't exist")
    public void testStartDriveShouldReturnInternalServerErrorBecauseDriveDoesNotExist() {
        ResponseEntity<TokenDTO> responseEntityTokenDTO =
                restTemplate.postForEntity("/auth/login-credentials",
                        new EmailPasswordLoginDTO("branko.lazic@gmail.com", "sifra123"),
                        TokenDTO.class);
        TokenDTO tokenDTO = responseEntityTokenDTO.getBody();
        String accessToken = tokenDTO.getAccessToken();

        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(5);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        HttpEntity<DriveDTO> httpEntity = new HttpEntity<>(driveDTO, httpHeaders);

        ResponseEntity<DriveDTO> responseEntity = restTemplate.exchange("/drives/start-drive", HttpMethod.PUT, httpEntity, DriveDTO.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Start drive - Should return INTERNAL_SERVER_ERROR status because driver is not on location")
    public void testStartDriveShouldReturnInternalServerErrorBecauseDriverIsNotOnLocation() {
        ResponseEntity<TokenDTO> responseEntityTokenDTO =
                restTemplate.postForEntity("/auth/login-credentials",
                        new EmailPasswordLoginDTO("vujadin.savic@gmail.com", "sifra123"),
                        TokenDTO.class);
        TokenDTO tokenDTO = responseEntityTokenDTO.getBody();
        String accessToken = tokenDTO.getAccessToken();

        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(3);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        HttpEntity<DriveDTO> httpEntity = new HttpEntity<>(driveDTO, httpHeaders);

        ResponseEntity<DriveDTO> responseEntity = restTemplate.exchange("/drives/start-drive", HttpMethod.PUT, httpEntity, DriveDTO.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("End drive - Should return OK status")
    public void testEndDriveShouldReturnOkStatus() {
        ResponseEntity<TokenDTO> responseEntityTokenDTO =
                restTemplate.postForEntity("/auth/login-credentials",
                        new EmailPasswordLoginDTO("vujadin.savic@gmail.com", "sifra123"),
                        TokenDTO.class);
        TokenDTO tokenDTO = responseEntityTokenDTO.getBody();
        String accessToken = tokenDTO.getAccessToken();

        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(3);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        HttpEntity<DriveDTO> httpEntity = new HttpEntity<>(driveDTO, httpHeaders);

        ResponseEntity<DriveDTO> responseEntity = restTemplate.exchange("/drives/end-drive", HttpMethod.PUT, httpEntity, DriveDTO.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("End drive - Should return INTERNAL_SERVER_ERROR status because drive doesn't exist")
    public void testEndDriveShouldReturnInternalServerErrorStatusBecauseDriveDoesNotExist() {
        ResponseEntity<TokenDTO> responseEntityTokenDTO =
                restTemplate.postForEntity("/auth/login-credentials",
                        new EmailPasswordLoginDTO("branko.lazic@gmail.com", "sifra123"),
                        TokenDTO.class);
        TokenDTO tokenDTO = responseEntityTokenDTO.getBody();
        String accessToken = tokenDTO.getAccessToken();

        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(5);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        HttpEntity<DriveDTO> httpEntity = new HttpEntity<>(driveDTO, httpHeaders);

        ResponseEntity<DriveDTO> responseEntity = restTemplate.exchange("/drives/end-drive", HttpMethod.PUT, httpEntity, DriveDTO.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("End drive - Should return INTERNAL_SERVER_ERROR status because driver is not on location")
    public void testEndDriveShouldReturnInternalServerErrorBecauseDriverIsNotOnLocation() {
        ResponseEntity<TokenDTO> responseEntityTokenDTO =
                restTemplate.postForEntity("/auth/login-credentials",
                        new EmailPasswordLoginDTO("branko.lazic@gmail.com", "sifra123"),
                        TokenDTO.class);
        TokenDTO tokenDTO = responseEntityTokenDTO.getBody();
        String accessToken = tokenDTO.getAccessToken();

        DriveDTO driveDTO = new DriveDTO();
        driveDTO.setId(4);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        HttpEntity<DriveDTO> httpEntity = new HttpEntity<>(driveDTO, httpHeaders);

        ResponseEntity<DriveDTO> responseEntity = restTemplate.exchange("/drives/end-drive", HttpMethod.PUT, httpEntity, DriveDTO.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
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
