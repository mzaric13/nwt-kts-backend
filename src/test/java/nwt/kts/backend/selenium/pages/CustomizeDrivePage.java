package nwt.kts.backend.selenium.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CustomizeDrivePage {

    @FindBy(id = "uber-passenger")
    private WebElement uberPassengerNavbarLink;

    @FindBy(id = "selectType")
    private WebElement selectVehicleType;

    @FindBy(id = "vehicle-type-6")
    private WebElement specificVehicleType;

    @FindBy(id = "swal2-title")
    private WebElement modalText;

    @FindBy(id = "createRide")
    private WebElement createRideBtn;

    @FindBy(id = "additionalDestinations")
    private WebElement checkBoxStartTime;

    @FindBy(id = "startTimeInput")
    private WebElement startTimeInput;

    @FindBy(className = "clock-face__container")
    private WebElement clockContainer;

    @FindBy(className = "timepicker-button")
    private WebElement timePickerButton;

    @FindBy(id = "personInput")
    private WebElement personInput;

    private WebDriver webDriver;

    public CustomizeDrivePage(WebDriver webDriver) {
        this.webDriver = webDriver;
        PageFactory.initElements(webDriver, this);
    }

    public void waitUntilLoaded() {
        new WebDriverWait(webDriver, Duration.ofSeconds(10).getSeconds()).until(ExpectedConditions.visibilityOf(uberPassengerNavbarLink));
    }

    public void clickSelectVehicleType() {
        this.selectVehicleType.click();
    }

    public void clickSpecificVehicleType() {
        this.specificVehicleType.click();
    }

    public void clickCreateRideBtn() {
        this.createRideBtn.click();
    }

    public void clickStartTimeCheckBox() {
        this.checkBoxStartTime.click();
    }

    public void clickStartTimeInput() {
        this.startTimeInput.click();
    }

    public boolean verifyModalIsLoaded(String message) {
        return (new WebDriverWait(webDriver, Duration.ofSeconds(10).getSeconds())
                .until(ExpectedConditions.textToBePresentInElement(modalText, message)));
    }

    public void chooseHour(int hour) {
        if (hour >= 1 && hour <= 12 && hour != 0) {
            WebElement spanHour = clockContainer.findElement(By.xpath(".//div["+hour+"]/span"));
            spanHour.click();
        } else {
            if (hour == 0) {
                hour = 24;
            }
            int pmHour = hour - 12;
            WebElement spanHour = clockContainer.findElement(By.xpath(".//div[13]/div["+pmHour+"]/span"));
            spanHour.click();
        }
    }

    public void chooseMinute(int minute) {
        WebElement spanMinute = clockContainer.findElement(By.xpath(".//div["+minute+"]/span"));
        Actions actions = new Actions(webDriver);
        actions.moveToElement(spanMinute, 10, 10).click().perform();
    }

    public void clickTimePickerButton() {
        WebElement button = this.timePickerButton.findElement(By.xpath("//span[contains(text(), 'Ok')]"));
        Actions actions = new Actions(webDriver);
        actions.moveToElement(button, 10, 10).click().perform();
    }

    public void inputPerson(String email) {
        this.personInput.sendKeys(email);
        this.personInput.sendKeys(Keys.ENTER);
    }

}
