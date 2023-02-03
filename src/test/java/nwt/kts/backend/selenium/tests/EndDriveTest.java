package nwt.kts.backend.selenium.tests;

import nwt.kts.backend.selenium.helper.Helper;
import nwt.kts.backend.selenium.pages.*;
import nwt.kts.backend.selenium.pages.drive.DriveExecutionPage;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.testng.Assert.assertTrue;

public class EndDriveTest extends TestBase {

    private String driverWindow;

    private String passengerWindow;

    @BeforeTest
    private void setup() {
        login();
        orderDrive();
        checkoutToMainWindow(driverWindow);
    }

    @Test
    public void testDriveNoNextDrive()  {

        DriveExecutionPage driveExecutionPage = new DriveExecutionPage(webDriver);
        boolean isLoaded = driveExecutionPage.isLoaded();
        assertTrue(isLoaded);

        driveExecutionPage.checkIfButtonDisabled();

        driveExecutionPage.pressStartDriveButton();
        Helper.takeScreenshoot(webDriver, "started-drive-screenshot");

        driveExecutionPage.pressEndDriveButton();
        Helper.takeScreenshoot(webDriver, "ended-drive-screenshot");

        DriverStartPage driverStartPage = new DriverStartPage(webDriver);
        isLoaded = driverStartPage.verifyPageIsLoaded("UBER-DRIVER");
        assertTrue(isLoaded);

    }

    private void login() {
        HomePage homePageDriver = new HomePage(webDriver);
        homePageDriver.waitUntilLoaded();
        homePageDriver.hoverLoginNavbarLink();
        homePageDriver.clickLoginNavbarLink();
        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.waitUntilLoaded();
        loginPage.enterEmail("branko.lazic@gmail.com");
        loginPage.enterPassword("sifra123");
        loginPage.pressLoginButton();
        ((JavascriptExecutor) webDriver).executeScript("window.open()");
        driverWindow = homePageDriver.getWindowHandle();
        homePageDriver.checkoutToAnotherWindow(Collections.singletonList(driverWindow));
        HomePage homePagePassenger = new HomePage(webDriver);
        homePagePassenger.waitUntilLoaded();
        homePagePassenger.hoverLoginNavbarLink();
        homePagePassenger.clickLoginNavbarLink();
        loginPage = new LoginPage(webDriver);
        loginPage.waitUntilLoaded();
        loginPage.enterEmail("darko.darkovic@gmail.com");
        loginPage.enterPassword("sifra123");
        loginPage.pressLoginButton();
        passengerWindow = homePagePassenger.getWindowHandle();
    }

    private void orderDrive() {
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
        customizeDrivePage.verifyModalIsLoaded("Drive consent");
        passengerStartPage.clickSwalConfirm();

        GiveConsentPage giveConsentPage = new GiveConsentPage(webDriver);
        giveConsentPage.verifyDriveAccepted();
    }

    private void checkoutToMainWindow(String mainWindowHandle) {
        webDriver.switchTo().window(mainWindowHandle);
    }

}
