package base;

import config.ConfigManager;
import base.DriverFactory;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.ScreenshotUtil;

import java.lang.reflect.Method;

/**
 * Base class for all test classes.
 * Handles driver lifecycle and post-test screenshot on failure.
 */
public abstract class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected static final ConfigManager config = ConfigManager.getInstance();

    /**
     * Logs a numbered test step to the console and to the Allure report timeline.
     *
     * <pre>{@code logStep(1, "Navigate to TEST CASES page");}</pre>
     */
    protected void logStep(int stepNumber, String description) {
        String message = "Step " + stepNumber + ": " + description;
        log.info(message);
        Allure.step(message);
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        log.info("▶ Starting test: {}", method.getName());
        Allure.label("thread", Thread.currentThread().getName());
        DriverFactory.createDriver();
        // Open the base URL so every test starts on the site — not a blank browser
        DriverFactory.getDriver().get(config.getBaseUrl());
        log.info("Opened base URL: {}", config.getBaseUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        String testName = result.getMethod().getMethodName();

        if (!result.isSuccess() && config.screenshotOnFailure()) {
            log.warn("✗ Test FAILED: {} — capturing screenshot", testName);
            try {
                ScreenshotUtil.captureAndAttach(testName);
            } catch (Exception e) {
                log.error("Could not capture screenshot: {}", e.getMessage());
            }
        } else if (result.isSuccess()) {
            log.info("✔ Test PASSED: {}", testName);
        } else {
            log.warn("⚠ Test SKIPPED: {}", testName);
        }

        DriverFactory.quitDriver();
    }
}
