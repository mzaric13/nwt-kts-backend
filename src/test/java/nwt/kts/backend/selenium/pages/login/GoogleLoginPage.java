package nwt.kts.backend.selenium.pages.login;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class GoogleLoginPage {

    @FindBy(id = "identifierId")
    private WebElement emailInput;

    @FindBy(xpath = "//input[@type='password']")
    private WebElement passwordInput;

    @FindBy(css = "button.VfPpkd-LgbsSe-OWXEXe-k8QpJ")
    private WebElement nextButton;

    private WebDriver webDriver;

    public GoogleLoginPage(WebDriver webDriver) {
        this.webDriver = webDriver;
        PageFactory.initElements(webDriver, this);
    }

    public void waitUntilEmailLoadedAndSendEmail(String email) {
        new WebDriverWait(webDriver, Duration.ofSeconds(10).getSeconds()).until(ExpectedConditions.visibilityOf(emailInput)).sendKeys(email);
    }

    public void pressNextButton() {
        nextButton.click();
    }

    public void waitUntilPasswordLoadedAndSendPassword(String password) {
        (new WebDriverWait(webDriver, Duration.ofSeconds(10).getSeconds()).until(ExpectedConditions.visibilityOf(passwordInput))).sendKeys(password);
    }
}
