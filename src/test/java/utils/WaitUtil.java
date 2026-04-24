package utils;

import base.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Standalone wait utilities for use outside page objects (e.g. listeners).
 */
public final class WaitUtil {

    private WaitUtil() {}

    public static WebElement waitForVisible(By locator, int timeoutSeconds) {
        WebDriver driver = DriverFactory.getDriver();
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static boolean waitForUrlContains(String fragment, int timeoutSeconds) {
        WebDriver driver = DriverFactory.getDriver();
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.urlContains(fragment));
    }
}