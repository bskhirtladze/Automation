package listeners;

import base.DriverFactory;
import config.ConfigManager;
import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(TestListener.class);
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Override
    public void onTestStart(ITestResult result) {
        log.info("▶  STARTED  : {}.{}",
                result.getTestClass().getRealClass().getSimpleName(),
                result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("✅ PASSED   : {}  ({} ms)",
                result.getName(), elapsed(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("❌ FAILED   : {}  ({} ms) — {}",
                result.getName(), elapsed(result),
                result.getThrowable() != null
                        ? result.getThrowable().getMessage()
                        : "unknown error");

        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver != null) {
                takeScreenshot(driver, result.getName());
                attachScreenshotToAllure(driver, result.getName());
            } else {
                log.warn("Driver is null — skipping screenshot for: {}", result.getName());
            }
        } catch (Exception e) {
            log.error("Screenshot failed: {}", e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("⏭  SKIPPED  : {}  — {}",
                result.getName(),
                result.getThrowable() != null
                        ? result.getThrowable().getMessage()
                        : "no reason given");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        log.warn("⚠️  WITHIN SUCCESS % : {}", result.getName());
    }

    private void takeScreenshot(WebDriver driver, String testName) {
        if (!ConfigManager.getInstance().screenshotOnFailure()) return;

        try {
            String timestamp = LocalDateTime.now().format(FMT);
            String fileName  = testName + "_" + timestamp + ".png";
            File   destDir   = new File(ConfigManager.getInstance().getScreenshotDir());
            File   destFile  = new File(destDir, fileName);

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(src, destFile);
            log.info("📸 Screenshot saved: {}", destFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to save screenshot: {}", e.getMessage());
        }
    }

    private void attachScreenshotToAllure(WebDriver driver, String testName) {
        try {
            byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(
                    "Screenshot on failure – " + testName,
                    "image/png",
                    new ByteArrayInputStream(bytes),
                    "png"
            );
            log.info("📎 Screenshot attached to Allure for: {}", testName);
        } catch (Exception e) {
            log.error("Failed to attach screenshot to Allure: {}", e.getMessage());
        }
    }

    private long elapsed(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}