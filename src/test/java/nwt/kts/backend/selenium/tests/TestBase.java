package nwt.kts.backend.selenium.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class TestBase {

    public static WebDriver webDriver;

    @BeforeSuite
    public void initializeWebDriver() {
        //C:\Users\T\AppData\Local\Google\Chrome\User Data\Default
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        //ChromeOptions options = new ChromeOptions();
        //options.addArguments("user-data-dir=C:/Users/T/AppData/Local/Google/Chrome/User Data");
        webDriver = new ChromeDriver(); //chrome
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    @AfterSuite
    public void quitDriver() {
        webDriver.quit();
    }
}
