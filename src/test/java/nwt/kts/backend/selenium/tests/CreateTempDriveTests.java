package nwt.kts.backend.selenium.tests;

import nwt.kts.backend.selenium.helper.Helper;
import nwt.kts.backend.selenium.pages.CustomizeDrivePage;
import nwt.kts.backend.selenium.pages.HomePage;
import nwt.kts.backend.selenium.pages.LoginPage;
import nwt.kts.backend.selenium.pages.PassengerStartPage;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Calendar;

public class CreateTempDriveTests extends TestBase {

    @BeforeTest
    public void loadPassengerHomePage() {
        HomePage homePage = new HomePage(webDriver);
        homePage.waitUntilLoaded();
        homePage.hoverLoginNavbarLink();
        homePage.clickLoginNavbarLink();
        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.waitUntilLoaded();
        loginPage.enterEmail("darko.darkovic@gmail.com");
        loginPage.enterPassword("sifra123");
        loginPage.pressLoginButton();
    }

    @Test
    public void pickupLocationDoesntExist() {
        PassengerStartPage passengerStartPage = new PassengerStartPage(webDriver);
        passengerStartPage.waitUntilLoaded();
        passengerStartPage.enterPickupLocation("I DO NOT EXIST");
        passengerStartPage.enterDestinationLocation("PUSKINOVA 27, NOVI SAD, NOVI SAD");
        passengerStartPage.clickSearchRoutesBtn();
        passengerStartPage.waitUntilModalIsLoaded("Oops...");
        Helper.takeScreenshoot(webDriver, "create_temp_drive_test_1_pickup_location_doesnt_exist");
    }

    @Test
    public void destinationLocationDoesntExist() {
        PassengerStartPage passengerStartPage = new PassengerStartPage(webDriver);
        passengerStartPage.waitUntilLoaded();
        passengerStartPage.enterPickupLocation("KISACKA 44, NOVI SAD, NOVI SAD");
        passengerStartPage.enterDestinationLocation("I DO NOT EXIST");
        passengerStartPage.clickSearchRoutesBtn();
        passengerStartPage.waitUntilModalIsLoaded("Oops...");
        Helper.takeScreenshoot(webDriver, "create_temp_drive_test_2_destination_location_doesnt_exist");
    }

    @Test
    public void customizeRide() {
        PassengerStartPage passengerStartPage = new PassengerStartPage(webDriver);
        passengerStartPage.waitUntilLoaded();
        passengerStartPage.enterPickupLocation("KISACKA 44, NOVI SAD, NOVI SAD");
        passengerStartPage.enterDestinationLocation("PUSKINOVA 27, NOVI SAD, NOVI SAD");
        passengerStartPage.clickSearchRoutesBtn();
        passengerStartPage.waitUntilCustomizeDriveBtnLoaded();
        passengerStartPage.clickCustomizeDriveBtn();

        CustomizeDrivePage customizeDrivePage = new CustomizeDrivePage(webDriver);
        customizeDrivePage.waitUntilLoaded();
        customizeDrivePage.clickSelectVehicleType();
        customizeDrivePage.clickSpecificVehicleType();
        customizeDrivePage.clickCreateRideBtn();
        customizeDrivePage.waitUntilModalIsLoaded("Success!");
        Helper.takeScreenshoot(webDriver, "create_temp_drive_test_3_customize_ride");
    }

    @Test
    public void driveReservationIsForThePast() {
        PassengerStartPage passengerStartPage = new PassengerStartPage(webDriver);
        passengerStartPage.waitUntilLoaded();
        passengerStartPage.enterPickupLocation("KISACKA 44, NOVI SAD, NOVI SAD");
        passengerStartPage.enterDestinationLocation("PUSKINOVA 27, NOVI SAD, NOVI SAD");
        passengerStartPage.clickSearchRoutesBtn();
        passengerStartPage.waitUntilCustomizeDriveBtnLoaded();
        passengerStartPage.clickCustomizeDriveBtn();

        CustomizeDrivePage customizeDrivePage = new CustomizeDrivePage(webDriver);
        customizeDrivePage.waitUntilLoaded();
        customizeDrivePage.clickSelectVehicleType();
        customizeDrivePage.clickSpecificVehicleType();
        customizeDrivePage.clickStartTimeCheckBox();
        customizeDrivePage.clickStartTimeInput();
        Calendar calendar = Calendar.getInstance();
        customizeDrivePage.chooseHour(calendar.get(Calendar.HOUR_OF_DAY));
        customizeDrivePage.chooseMinute(calendar.get(Calendar.MINUTE) - 10);
        customizeDrivePage.clickTimePickerButton();
        customizeDrivePage.clickCreateRideBtn();
        customizeDrivePage.waitUntilModalIsLoaded("Error!");
        Helper.takeScreenshoot(webDriver, "create_temp_drive_test_4_invalid_start_time_past");
    }

    @Test
    public void driveReservationIsForTimeMoreThanFiveHoursAway() {
        PassengerStartPage passengerStartPage = new PassengerStartPage(webDriver);
        passengerStartPage.waitUntilLoaded();
        passengerStartPage.enterPickupLocation("KISACKA 44, NOVI SAD, NOVI SAD");
        passengerStartPage.enterDestinationLocation("PUSKINOVA 27, NOVI SAD, NOVI SAD");
        passengerStartPage.clickSearchRoutesBtn();
        passengerStartPage.waitUntilCustomizeDriveBtnLoaded();
        passengerStartPage.clickCustomizeDriveBtn();

        CustomizeDrivePage customizeDrivePage = new CustomizeDrivePage(webDriver);
        customizeDrivePage.waitUntilLoaded();
        customizeDrivePage.clickSelectVehicleType();
        customizeDrivePage.clickSpecificVehicleType();
        customizeDrivePage.clickStartTimeCheckBox();
        customizeDrivePage.clickStartTimeInput();
        Calendar calendar = Calendar.getInstance();
        customizeDrivePage.chooseHour(calendar.get(Calendar.HOUR_OF_DAY) + 5);
        customizeDrivePage.chooseMinute(calendar.get(Calendar.MINUTE) + 10);
        customizeDrivePage.clickTimePickerButton();
        customizeDrivePage.clickCreateRideBtn();
        customizeDrivePage.waitUntilModalIsLoaded("Error!");
        Helper.takeScreenshoot(webDriver, "create_temp_drive_test_5_invalid_start_time_future");
    }

    @Test
    public void driveReservationHasInvalidPassengerEmail() {
        PassengerStartPage passengerStartPage = new PassengerStartPage(webDriver);
        passengerStartPage.waitUntilLoaded();
        passengerStartPage.enterPickupLocation("KISACKA 44, NOVI SAD, NOVI SAD");
        passengerStartPage.enterDestinationLocation("PUSKINOVA 27, NOVI SAD, NOVI SAD");
        passengerStartPage.clickSearchRoutesBtn();
        passengerStartPage.waitUntilCustomizeDriveBtnLoaded();
        passengerStartPage.clickCustomizeDriveBtn();

        CustomizeDrivePage customizeDrivePage = new CustomizeDrivePage(webDriver);
        customizeDrivePage.waitUntilLoaded();
        customizeDrivePage.clickSelectVehicleType();
        customizeDrivePage.clickSpecificVehicleType();
        customizeDrivePage.inputPerson("dsdasdsadas@gmail.com");
        customizeDrivePage.clickCreateRideBtn();
        customizeDrivePage.waitUntilModalIsLoaded("Error!");
        Helper.takeScreenshoot(webDriver, "create_temp_drive_test_6_invalid_passenger_email");
    }

}
