package nwt.kts.backend.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    @FindBy(id = "facebook")
    private WebElement facebookButton;

    @FindBy(id = "loginButton")
    private WebElement loginButton;

    @FindBy(id = "googleButtonDiv")
    private WebElement googleButton;

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "swal2-title")
    private WebElement modalText;

    private WebDriver webDriver;

    public LoginPage(WebDriver webDriver) {
        this.webDriver = webDriver;
        PageFactory.initElements(webDriver, this);
    }

    public void waitUntilLoaded() {
        new WebDriverWait(webDriver, Duration.ofSeconds(10).getSeconds()).until(ExpectedConditions.elementToBeClickable(facebookButton));
    }

    public void pressLoginButton() {
        loginButton.submit();
    }

    public void pressGoogleButton() {
        googleButton.click();
    }

    public void pressFacebookButton() {
        facebookButton.click();
    }

    public void enterEmail(String email) {
        emailInput.sendKeys(email);
    }

    public void enterPassword(String password) {
        passwordInput.sendKeys(password);
    }

    public boolean waitUntilModalIsLoaded(String message) {
        return (new WebDriverWait(webDriver, Duration.ofSeconds(10).getSeconds())
                .until(ExpectedConditions.textToBePresentInElement(modalText, message)));
    }

    public String getWindowHandle() {
        return webDriver.getWindowHandle();
    }

    public void checkoutToAnotherWindow(String mainWindowHandle) {
        for (String windowHandle: webDriver.getWindowHandles()) {
            if (!mainWindowHandle.equals(windowHandle)) {
                webDriver.switchTo().window(windowHandle);
            }
        }
    }

    public void checkoutToMainWindow(String mainWindowHandle) {
        webDriver.switchTo().window(mainWindowHandle);
    }
}
