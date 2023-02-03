package nwt.kts.backend.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class GiveConsentPage {

    @FindBy(id = "accept-title")
    private WebElement acceptTitle;

    @FindBy(id = "decline-title")
    private WebElement declineTitle;

    private WebDriver webDriver;

    public GiveConsentPage(WebDriver webDriver) {
        this.webDriver = webDriver;
        PageFactory.initElements(webDriver, this);
    }

    public boolean verifyDriveAccepted() {
        return (new WebDriverWait(webDriver, Duration.ofSeconds(10).getSeconds()).until(ExpectedConditions.visibilityOf(acceptTitle))).isDisplayed();
    }

    public boolean verifyDriveRejected() {
        return (new WebDriverWait(webDriver, Duration.ofSeconds(10).getSeconds()).until(ExpectedConditions.visibilityOf(declineTitle))).isDisplayed();
    }
}
