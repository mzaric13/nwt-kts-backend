package nwt.kts.backend.selenium.pages.login;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AdminStartPage {

    public AdminStartPage(WebDriver webDriver) {
        this.webDriver = webDriver;
        PageFactory.initElements(webDriver, this);
    }

    private WebDriver webDriver;

    @FindBy(id = "uber-admin")
    private WebElement uberAdminNavbarLink;

    public void waitUntilLoaded() {
        new WebDriverWait(webDriver, Duration.ofSeconds(10).getSeconds()).until(ExpectedConditions.visibilityOf(uberAdminNavbarLink));
    }
}
