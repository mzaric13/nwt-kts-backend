package nwt.kts.backend.selenium.tests;

import nwt.kts.backend.selenium.helper.Helper;
import nwt.kts.backend.selenium.pages.*;
import nwt.kts.backend.selenium.pages.login.AdminStartPage;
import nwt.kts.backend.selenium.pages.login.FacebookLoginPage;
import nwt.kts.backend.selenium.pages.login.GoogleLoginPage;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class LoginTests extends TestBase {

    @BeforeTest
    public void loadHomePage() {
        HomePage homePage = new HomePage(webDriver);
        homePage.waitUntilLoaded();
        homePage.hoverLoginNavbarLink();
        homePage.clickLoginNavbarLink();
    }

    @Test
    public void unsuccessfulLoginNoCredentialsEntered() {
        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.waitUntilLoaded();
        loginPage.pressLoginButton();
        assertTrue(loginPage.waitUntilModalIsLoaded("Error: Email and password not given."));
        Helper.takeScreenshoot(webDriver, "login_test_1_no_credentials_entered");
    }

    @Test
    public void unsuccessfulLoginWrongEmailEntered() {
        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.waitUntilLoaded();
        loginPage.enterEmail("wrong.email@gmail.com");
        loginPage.enterPassword("sifra123");
        loginPage.pressLoginButton();
        assertTrue(loginPage.waitUntilModalIsLoaded("User with given credentials doesn't exist!"));
        Helper.takeScreenshoot(webDriver, "login_test_2_wrong_email_entered");
    }

    @Test
    public void unsuccessfulLoginWrongPasswordEntered() {
        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.waitUntilLoaded();
        loginPage.enterEmail("darko.darkovic@gmail.com");
        loginPage.enterPassword("losasifra123");
        loginPage.pressLoginButton();
        assertTrue(loginPage.waitUntilModalIsLoaded("User with given credentials doesn't exist!"));
        Helper.takeScreenshoot(webDriver, "login_test_3_wrong_password_entered");
    }

    @Test
    public void successfulLoginPassenger() {
        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.waitUntilLoaded();
        loginPage.enterEmail("darko.darkovic@gmail.com");
        loginPage.enterPassword("sifra123");
        loginPage.pressLoginButton();

        PassengerStartPage passengerStartPage = new PassengerStartPage(webDriver);
        assertTrue(passengerStartPage.verifyPageIsLoaded("UBER-PASSENGER"));
        Helper.takeScreenshoot(webDriver, "login_test_4_successful_login_passenger");
    }

    @Test
    public void successfulLoginDriver() {
        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.waitUntilLoaded();
        loginPage.enterEmail("mirko.ivanic@gmail.com");
        loginPage.enterPassword("sifra123");
        loginPage.pressLoginButton();

        DriverStartPage driverStartPage = new DriverStartPage(webDriver);
        assertTrue(driverStartPage.verifyPageIsLoaded("UBER-DRIVER"));
        Helper.takeScreenshoot(webDriver, "login_test_5_successful_login_driver");
    }

    @Test
    public void successfulLoginAdministrator() {
        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.waitUntilLoaded();
        loginPage.enterEmail("admin.admin@gmail.com");
        loginPage.enterPassword("sifra123");
        loginPage.pressLoginButton();

        AdminStartPage adminStartPage = new AdminStartPage(webDriver);
        assertTrue(adminStartPage.verifyPageIsLoaded("UBER-ADMIN"));
        Helper.takeScreenshoot(webDriver, "login_test_6_successful_login_administrator");
    }

    //Doesn't work because of google authorization
    //googleSeleniumTest.ipynb(.py) script - working selenium test
    //add email and password afterwards, so gmail doesn't get hacked :)
    /*@Test
    public void googleLogin() {

        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.waitUntilLoaded();
        String currentWindow = loginPage.getWindowHandle();
        loginPage.pressGoogleButton();
        loginPage.checkoutToAnotherWindow(currentWindow);

        GoogleLoginPage googleLoginPage = new GoogleLoginPage(webDriver);
        googleLoginPage.waitUntilEmailLoadedAndSendEmail("dummy_email");
        googleLoginPage.pressNextButton();
        googleLoginPage.waitUntilPasswordLoadedAndSendPassword("dummy_password");
        googleLoginPage.pressNextButton();

        PassengerStartPage passengerStartPage = new PassengerStartPage(webDriver);
        assertTrue(passengerStartPage.verifyPageIsLoaded("UBER-PASSENGER"));
        Helper.takeScreenshoot(webDriver, "login_test_8_successful_google_passenger");

    }*/

    @Test
    //add email and password afterwards, so fb doesn't get hacked :)
    public void facebookLogin() {
        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.waitUntilLoaded();
        String mainWindow = loginPage.getWindowHandle();
        loginPage.pressFacebookButton();
        loginPage.checkoutToAnotherWindow(mainWindow);

        FacebookLoginPage facebookLoginPage = new FacebookLoginPage(webDriver);
        facebookLoginPage.waitUntilLoaded();
        facebookLoginPage.enterEmail("dummy_email");
        facebookLoginPage.enterPassword("dummy_password");
        facebookLoginPage.pressLoginButton();
        facebookLoginPage.pressBackLink();

        loginPage.checkoutToMainWindow(mainWindow);

        PassengerStartPage passengerStartPage = new PassengerStartPage(webDriver);
        assertTrue(passengerStartPage.verifyPageIsLoaded("UBER-PASSENGER"));
        Helper.takeScreenshoot(webDriver, "login_test_7_successful_facebook_passenger");

    }
}
