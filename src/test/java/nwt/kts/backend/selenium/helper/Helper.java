package nwt.kts.backend.selenium.helper;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.FileHandler;

import java.io.File;
import java.io.IOException;

public class Helper {
    public static void takeScreenshoot(WebDriver driver, String name) {
        TakesScreenshot ts = (TakesScreenshot) driver;

        //capture screenshot as output type FILE
        File file = ts.getScreenshotAs(OutputType.FILE);
        try {
            //save the screenshot taken in destination path
            FileHandler.copy(file, new File("./screenShots/" + name + ".png"));
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}
