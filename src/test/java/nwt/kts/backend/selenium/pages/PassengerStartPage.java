package nwt.kts.backend.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PassengerStartPage {

    @FindBy(id = "uber-passenger")
    private WebElement uberPassengerNavbarLink;

    @FindBy(id = "pickup")
    private WebElement pickupLocationInput;

    @FindBy(id = "destination")
    private WebElement destinationLocationInput;

    @FindBy(id = "searchRoutes")
    private WebElement searchRoutesBtn;

    @FindBy(id = "customizeDrive")
    private WebElement customizeDriveBtn;

    @FindBy(id = "swal2-title")
    private WebElement modalText;

    private WebDriver webDriver;

    public PassengerStartPage(WebDriver webDriver) {
        this.webDriver = webDriver;
        PageFactory.initElements(webDriver, this);
    }

    public void waitUntilLoaded() {
        new WebDriverWait(webDriver, Duration.ofSeconds(10).getSeconds()).until(ExpectedConditions.visibilityOf(uberPassengerNavbarLink));
    }

    public void enterPickupLocation(String location) {
        this.pickupLocationInput.sendKeys(location);
    }

    public void enterDestinationLocation(String location) {
        this.destinationLocationInput.sendKeys(location);
    }

    public void clickSearchRoutesBtn() {
        this.searchRoutesBtn.click();
    }

    public void clickCustomizeDriveBtn() {
        this.customizeDriveBtn.click();
    }

    public void waitUntilCustomizeDriveBtnLoaded() {
        new WebDriverWait(webDriver,
                Duration.ofSeconds(10).getSeconds()).until(ExpectedConditions.elementToBeClickable(customizeDriveBtn)).isEnabled();
    }

    public void waitUntilModalIsLoaded(String message) {
        new WebDriverWait(webDriver, Duration.ofSeconds(10).getSeconds())
                .until(ExpectedConditions.textToBePresentInElement(modalText, message));
    }
}
