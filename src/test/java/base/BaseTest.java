package base;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.ConfigReader;
import utils.ScreenshotUtil;
import utils.TestContext;

import java.lang.reflect.Method;

public class BaseTest {
    protected WebDriver driver;
    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {

        TestContext.setTestName(method.getName());
        driver = DriverFactory.initDriver();
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.get(ConfigReader.get("base.url"));
        driver.manage().deleteAllCookies();
        System.out.println("[" + TestContext.getTestName() + "] === Test Started ===");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            ScreenshotUtil.capture(driver, result.getName());
        }
        if (result.getThrowable() != null) {
            Allure.addAttachment("Error", result.getThrowable().toString());
        }
        System.out.println("=== Test Finished: " + result.getName() + " ===");
        DriverFactory.quitDriver();
    }

    public static void logStep(int step, String message) {
        logger.info("STEP {}: {}", step, message);
    }
}
