package nwt.kts.backend.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DriverStartPage {

    @FindBy(id = "uber-driver")
    private WebElement uberDriverNavbarLink;

    private WebDriver webDriver;

    public DriverStartPage(WebDriver webDriver) {
        this.webDriver = webDriver;
        PageFactory.initElements(webDriver, this);
    }

    public boolean verifyPageIsLoaded(String text) {
        return (new WebDriverWait(webDriver, Duration.ofSeconds(10).getSeconds()).until(ExpectedConditions.textToBePresentInElement(uberDriverNavbarLink, text)));
    }

}
