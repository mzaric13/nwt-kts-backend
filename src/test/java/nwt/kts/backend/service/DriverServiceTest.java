package nwt.kts.backend.service;


import nwt.kts.backend.entity.*;
import nwt.kts.backend.repository.DriveRepository;
import nwt.kts.backend.repository.DriverRepository;
import nwt.kts.backend.service.DriverService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.RowId;
import java.util.*;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class DriverServiceTest {

    @Mock
    private DriveRepository driveRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverService driverService;

    @Test
    @DisplayName("Should return available driver")
    public void testFindDriverAvailable() {
        Role role = new Role(1, "ROLE_DRIVER");
        Type type = new Type(1, "Sedan");
        Vehicle vehicle = new Vehicle(1, "NS123PO", "Audi A4", type);
        Point location = new Point(45.123, 12.123);
        Driver driver = new Driver(1, "vozac@gmail.com", "0628885151",
                "$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy", "Pero", "Peric",
                "Novi Sad", role, false, true, vehicle, location);
        List<Point> waypoints = new ArrayList<>();
        waypoints.add(new Point(45.245, 13.23));
        waypoints.add(new Point(45.234, 11.234));
        Route route = new Route("Route name", 30.2, 1154, waypoints, 0);
        TempDrive tempDrive = new TempDrive(new Timestamp(new Date().getTime()), 720, 1154,
                new HashSet<>(), new HashSet<>(), route, type);
        List<Driver> drivers = Collections.singletonList(driver);

        when(driverRepository.findDriversByIsAvailable(true)).thenReturn(drivers);

        Driver selected = driverService.selectDriverForDrive(tempDrive);
        assertEquals(selected.getId(), 1);
        verify(driverRepository, times(1)).findDriversByIsAvailable(true);
    }

    @Test
    @DisplayName("Find available with lower distance")
    public void testFindDriverAvailableLowerDistance() {
        Role role = new Role(1, "ROLE_DRIVER");
        Type type = new Type(1, "Sedan");
        Vehicle vehicle = new Vehicle(1, "NS123PO", "Audi A4", type);
        Point location = new Point(45.123, 12.123);
        Driver driver1 = new Driver(1, "vozac1@gmail.com", "0628885151",
                "$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy", "Pero", "Peric",
                "Novi Sad", role, false, true, vehicle, location);
        Driver driver2 = new Driver(2, "vozac2@gmail.com", "0628885151",
                "$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy", "Marko", "Markovic",
                "Novi Sad", role, false, true, vehicle, new Point(45.244, 11.456));
        List<Point> waypoints = new ArrayList<>();
        waypoints.add(new Point(45.245, 13.23));
        waypoints.add(new Point(45.234, 11.234));
        Route route = new Route("Route name", 30.2, 1154, waypoints, 0);
        TempDrive tempDrive = new TempDrive(new Timestamp(new Date().getTime()), 720, 1154,
                new HashSet<>(), new HashSet<>(), route, type);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver1);
        drivers.add(driver2);

        when(driverRepository.findDriversByIsAvailable(true)).thenReturn(drivers);

        Driver selected = driverService.selectDriverForDrive(tempDrive);
        assertEquals(2, selected.getId());
        verify(driverRepository, times(1)).findDriversByIsAvailable(true);
    }

    @Test
    @DisplayName("Find non-available driver")
    public void testFindNonAvailableClosestToRouteEnd() {
        Role role = new Role(1, "ROLE_DRIVER");
        Type type = new Type(1, "Sedan");
        Vehicle vehicle = new Vehicle(1, "NS123TX", "Audi A4", type);
        Driver driver1 = new Driver(1, "vozac1@gmail.com", "0628885151",
                "$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy", "Pero", "Peric",
                "Novi Sad", role, false, false, vehicle, new Point(45.212, 11.422));
        driver1.setTimeOfLogin(new Timestamp(new Date().getTime()));
        driver1.setHasFutureDrive(false);
        List<Point> waypoints1 = new ArrayList<>();
        waypoints1.add(new Point(45.245, 13.23));
        waypoints1.add(new Point(45.234, 11.234));
        List<Point> waypoints2 = new ArrayList<>();
        waypoints2.add(new Point(45.256, 12.234));
        waypoints2.add(new Point(45.234, 12.134));
        Route route1 = new Route("Route name", 30.2, 1154, waypoints1, 0);
        Route route2 = new Route("Route 2 name", 25.2, 984, waypoints2, 0);
        TempDrive tempDrive = new TempDrive(new Timestamp(new Date().getTime()), 720, 1154,
                new HashSet<>(), new HashSet<>(), route1, type);
        Drive drive1 = new Drive(1, new Timestamp(new Date().getTime()), new Timestamp(new Date().getTime()), 700,
                1223, new ArrayList<>(), new HashSet<>(), Status.STARTED, driver1, new HashSet<>(), route2);

        when(driverRepository.findDriversByIsAvailable(true)).thenReturn(new ArrayList<>());
        when(driverRepository.findDriversByIsAvailable(false)).thenReturn(Collections.singletonList(driver1));
        when(driveRepository.findFirstByDriverAndStatusOrderByIdDesc(driver1, Status.STARTED)).thenReturn(Optional.of(drive1));
        when(driverRepository.save(driver1)).thenReturn(driver1);

        Driver selected = driverService.selectDriverForDrive(tempDrive);
        assertEquals(1, selected.getId());
        assertTrue(selected.isHasFutureDrive());
        verify(driverRepository, times(1)).findDriversByIsAvailable(false);
    }

    @Test
    @DisplayName("No available drivers due to no drivers with given vehicle type")
    public void testNoAvailableDriversWithGivenVehicleType() {
        Role role = new Role(1, "ROLE_DRIVER");
        Vehicle vehicle1 = new Vehicle(1, "NS123PO", "Audi A4", new Type(1, "Sedan"));
        Vehicle vehicle2 = new Vehicle(2, "NS124PO", "Audi Q5", new Type(2, "SUV"));
        Driver driver1 = new Driver(1, "vozac1@gmail.com", "0628885151",
                "$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy", "Pero", "Peric",
                "Novi Sad", role, false, true, vehicle1, new Point(45.123, 12.123));
        Driver driver2 = new Driver(2, "vozac2@gmail.com", "0628885151",
                "$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy", "Marko", "Markovic",
                "Novi Sad", role, false, false, vehicle2, new Point(45.244, 11.456));
        driver2.setTimeOfLogin(new Timestamp(new Date().getTime()));
        driver2.setHasFutureDrive(false);
        List<Point> waypoints1 = new ArrayList<>();
        waypoints1.add(new Point(45.245, 13.23));
        waypoints1.add(new Point(45.234, 11.234));
        Route route1 = new Route("Route name", 30.2, 1154, waypoints1, 0);
        TempDrive tempDrive = new TempDrive(new Timestamp(new Date().getTime()), 720, 1154,
                new HashSet<>(), new HashSet<>(), route1, new Type(3, "Hatchback"));

        when(driverRepository.findDriversByIsAvailable(true)).thenReturn(Collections.singletonList(driver1));
        when(driverRepository.findDriversByIsAvailable(false)).thenReturn(Collections.singletonList(driver2));

        Driver selected = driverService.selectDriverForDrive(tempDrive);
        assertNull(selected);
        verify(driverRepository, times(1)).findDriversByIsAvailable(true);
        verify(driverRepository, times(1)).findDriversByIsAvailable(false);
    }

    @Test
    @DisplayName("No available drivers because they are not logged in")
    public void testNoDriverBecauseOfNoOneIsLoggedIn() {
        Role role = new Role(1, "ROLE_DRIVER");
        Type type = new Type(1, "Sedan");
        Vehicle vehicle1 = new Vehicle(1, "NS123PO", "Audi A4", type);
        Driver driver1 = new Driver(1, "vozac1@gmail.com", "0628885151",
                "$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy", "Pero", "Peric",
                "Novi Sad", role, false, false, vehicle1, new Point(45.123, 12.123));
        TempDrive tempDrive = new TempDrive(new Timestamp(new Date().getTime()), 720, 1154,
                new HashSet<>(), new HashSet<>(), new Route(), type);

        when(driverRepository.findDriversByIsAvailable(true)).thenReturn(new ArrayList<>());
        when(driverRepository.findDriversByIsAvailable(false)).thenReturn(Collections.singletonList(driver1));

        Driver selected = driverService.selectDriverForDrive(tempDrive);
        assertNull(selected);
        verify(driverRepository, times(1)).findDriversByIsAvailable(true);
        verify(driverRepository, times(1)).findDriversByIsAvailable(false);
    }

    @Test
    @DisplayName("No available drivers because they have next drive")
    public void testNoAvailableDriversBecauseEveryoneHasNextDrive() {
        Role role = new Role(1, "ROLE_DRIVER");
        Type type = new Type(1, "Sedan");
        Vehicle vehicle1 = new Vehicle(1, "NS123PO", "Audi A4", type);
        Driver driver1 = new Driver(1, "vozac1@gmail.com", "0628885151",
                "$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy", "Pero", "Peric",
                "Novi Sad", role, false, false, vehicle1, new Point(45.123, 12.123));
        driver1.setTimeOfLogin(new Timestamp(new Date().getTime()));
        driver1.setHasFutureDrive(true);
        TempDrive tempDrive = new TempDrive(new Timestamp(new Date().getTime()), 720, 1154,
                new HashSet<>(), new HashSet<>(), new Route(), type);

        when(driverRepository.findDriversByIsAvailable(true)).thenReturn(new ArrayList<>());
        when(driverRepository.findDriversByIsAvailable(false)).thenReturn(Collections.singletonList(driver1));

        Driver selected = driverService.selectDriverForDrive(tempDrive);
        assertNull(selected);
        verify(driverRepository, times(1)).findDriversByIsAvailable(true);
        verify(driverRepository, times(1)).findDriversByIsAvailable(false);
    }

    @Test
    @DisplayName("Check drivers work time")
    public void checkDriversWorkTime() {
        Driver driver = new Driver(1, "vozac1@gmail.com", "0628885151",
                "$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy", "Pero", "Peric",
                "Novi Sad", new Role(1, "ROLE_DRIVER"), false, true, new Vehicle(),
                new Point(45.123, 12.123));
        Date loginDate = new Date(2023, new Date().getMonth(), new Date().getDate(), new Date().getHours() - 4, 0);
        driver.setTimeOfLogin(new Timestamp(loginDate.getTime()));

        when(driverRepository.findAll()).thenReturn(Collections.singletonList(driver));

        driverService.checkDriversWorkTime();

        assertNotNull(driver.getTimeOfLogin());
        assertTrue(driver.isAvailable());
    }

    @Test
    @DisplayName("Check drivers work time and end work")
    public void checkDriversWorkTimeAndEndWork() {
        Driver driver = new Driver(1, "vozac1@gmail.com", "0628885151",
                "$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy", "Pero", "Peric",
                "Novi Sad", new Role(1, "ROLE_DRIVER"), false, true, new Vehicle(),
                new Point(45.123, 12.123));
        Date loginDate = new Date(new Date().getYear(), new Date().getMonth(), new Date().getDate(), new Date().getHours() - 8, 0);
        driver.setTimeOfLogin(new Timestamp(loginDate.getTime()));

        when(driverRepository.findAll()).thenReturn(Collections.singletonList(driver));

        driverService.checkDriversWorkTime();

        assertNull(driver.getTimeOfLogin());
        assertFalse(driver.isAvailable());
    }
}
