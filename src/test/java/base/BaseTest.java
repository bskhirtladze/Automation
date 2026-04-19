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
import utils.LoggerUtil;
import utils.ScreenshotUtil;
import utils.TestContext;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import static utils.LoggerUtil.log;

public class BaseTest {

    protected WebDriver driver;
    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {

        // ✔ set test name (single source of truth)
        TestContext.setTestName(method.getName());

        driver = DriverFactory.initDriver();

        driver.manage().window().setSize(new Dimension(1920, 1080));

        driver.get(ConfigReader.get("base.url"));

        driver.manage().deleteAllCookies();

        // ❌ no need to add testName here
        log("=== Test Started ===");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {

        try {

            if (result.getStatus() == ITestResult.FAILURE) {

                String testName = TestContext.getTestName();

                // 📸 Screenshot
                byte[] screenshot = ScreenshotUtil.captureAsBytes(driver);
                Allure.addAttachment(
                        "Screenshot - " + testName,
                        new ByteArrayInputStream(screenshot)
                );

                // ❌ Error
                if (result.getThrowable() != null) {

                    StringWriter sw = new StringWriter();
                    result.getThrowable().printStackTrace(new PrintWriter(sw));

                    Allure.addAttachment(
                            "Error - " + testName,
                            result.getThrowable().toString()
                    );

                    Allure.addAttachment(
                            "Stacktrace - " + testName,
                            sw.toString()
                    );
                }
            }

        } finally {

            String testName = TestContext.getTestName();

            // 🚀 TEST FINISHED LOG (ADD THIS)
            LoggerUtil.log("=== Test Finished ===");

            if (driver != null) {
                DriverFactory.quitDriver();
            }

            TestContext.clear(); // 🔥 IMPORTANT FIX (memory/thread safety)
        }
    }

    public static void logStep(int step, String message) {
        logger.info("STEP {}: {}", step, message);
    }
}