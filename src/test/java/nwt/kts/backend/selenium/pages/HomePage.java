package nwt.kts.backend.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePage {

    @FindBy(id = "login")
    private WebElement loginNavbarLink;

    @FindBy(tagName = "strong")
    private WebElement uberStrongTag;

    private WebDriver webDriver;
    private static String PAGE_URL = "http://localhost:4200/home-guest";

    public HomePage(WebDriver webDriver) {
        this.webDriver = webDriver;
        webDriver.navigate().to(PAGE_URL);

        PageFactory.initElements(webDriver, this);
    }

    public void hoverLoginNavbarLink() {
        Actions action = new Actions(webDriver);
        action.moveToElement(loginNavbarLink).perform();
    }

    public void clickLoginNavbarLink() {
        loginNavbarLink.click();
    }

    public void waitUntilLoaded() {
        new WebDriverWait(webDriver, Duration.ofSeconds(10).getSeconds()).until(ExpectedConditions.textToBePresentInElement(uberStrongTag, "UBER"));
    }

}
