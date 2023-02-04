package nwt.kts.backend.selenium.pages.drive;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class DriveExecutionPage {

    @FindBy(id = "startCancelText")
    private WebElement startCancelText;

    @FindBy(id = "routeDetails")
    private WebElement routeDetails;

    @FindBy(id = "startDriveButton")
    private WebElement startDriveButton;

    @FindBy(id = "endDriveButton")
    private WebElement endDriveButton;

    private WebDriver webDriver;

    public DriveExecutionPage(WebDriver webDriver) {
        this.webDriver = webDriver;
        PageFactory.initElements(webDriver, this);
    }

    public boolean isLoaded() {
        return (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.textToBePresentInElement(startCancelText, "Start or cancel the drive"));
    }

    public boolean isLoadedPassenger() {
        return (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.textToBePresentInElement(routeDetails, "Details"));
    }

    public void checkIfButtonDisabled() {
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(10).getSeconds());
        webDriverWait.until(ExpectedConditions.attributeToBe(startDriveButton, "disabled", "true"));
    }

    public void pressStartDriveButton() {
        new WebDriverWait(webDriver, Duration.ofSeconds(120).getSeconds())
                .until(ExpectedConditions.elementToBeClickable(startDriveButton)).click();
    }

    public void pressEndDriveButton() {
        new WebDriverWait(webDriver, Duration.ofSeconds(300).getSeconds())
                .until(ExpectedConditions.elementToBeClickable(endDriveButton)).submit();
    }

    public void checkoutToAnotherWindow(List<String> windowHandles) {
        for (String windowHandle: webDriver.getWindowHandles()) {
            if (!windowHandles.contains(windowHandle)) {
                webDriver.switchTo().window(windowHandle);
            }
        }
    }
}
