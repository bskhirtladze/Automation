package base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.ConfigReader;
import utils.ScreenshotUtil;

public class BaseTest {
    protected WebDriver driver;
    private static final Logger logger = LogManager.getLogger(BasePage.class);

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        // 1. Driver init (thread-safe)
        driver = DriverFactory.initDriver();

        // 2. Browser setup
        driver.manage().window().maximize();

        // 3. Open base URL from config
        driver.get(ConfigReader.get("base.url"));

        // 4. Optional: clear cookies
        driver.manage().deleteAllCookies();

        // 5. Optional: logging
        System.out.println("=== Test Started ===");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {

        // 1. Screenshot on failure
        if (result.getStatus() == ITestResult.FAILURE) {
            ScreenshotUtil.capture(driver, result.getName());
        }

        // 2. Logging
        System.out.println("=== Test Finished: " + result.getName() + " ===");

        // 3. Quit driver
        if (driver != null) {
            driver.quit();
        }
    }

    public static void logStep(int step, String message) {
        logger.info("STEP {}: {}", step, message);
    }
}
