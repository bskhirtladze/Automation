package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DriverFactory {
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver initDriver() {
        WebDriverManager.chromedriver().setup();
        driver.set(new ChromeDriver());
        return driver.get();
    }

    public static WebDriver getDriver() {
        return driver.get();
    }
}
