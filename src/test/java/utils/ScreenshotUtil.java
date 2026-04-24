package utils;

import base.DriverFactory;
import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * Utility for capturing screenshots and attaching them to Allure reports.
 *
 * On-disk file is named after the test only (no timestamp), so each run
 * overwrites the previous screenshot — only the latest failure is kept.
 * The Allure attachment is always preserved in the report regardless.
 */
public final class ScreenshotUtil {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtil.class);

    private ScreenshotUtil() {}

    /**
     * Takes a screenshot, saves it to disk (overwriting any previous file for
     * this test), and attaches it to the Allure report.
     *
     * @param testName the test method name — used as the filename and Allure label
     */
    public static void captureAndAttach(String testName) {
        WebDriver driver = DriverFactory.getDriver();
        byte[] screenshotBytes = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.BYTES);

        // Attach to Allure report
        Allure.addAttachment(
                "Screenshot – " + testName,
                "image/png",
                new ByteArrayInputStream(screenshotBytes),
                "png"
        );

        // Save to disk — fixed name per test, overwrites previous run
        String filename = testName.replaceAll("[^a-zA-Z0-9_-]", "_") + ".png";
        File destFile = new File(config.ConfigManager.getInstance().getScreenshotDir(), filename);

        try {
            FileUtils.writeByteArrayToFile(destFile, screenshotBytes);
            log.info("Screenshot saved to {}", destFile.getAbsolutePath());
        } catch (IOException e) {
            log.warn("Could not save screenshot to disk: {}", e.getMessage());
        }
    }
}
