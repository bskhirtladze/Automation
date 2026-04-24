package base;

import config.ConfigManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Thread-safe WebDriver factory using ThreadLocal.
 * Supports chrome; reads config from ConfigManager.
 */
public class DriverFactory {

    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverFactory() {}

    public static void createDriver() {
        log.info("Creating 'chrome' driver (headless={})",
                ConfigManager.getInstance().isHeadless());

        WebDriver driver = createChromeDriver();

        int implicitWait    = ConfigManager.getInstance().getImplicitWait();
        int pageLoadTimeout = ConfigManager.getInstance().getPageLoadTimeout();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        driver.manage().window().maximize();

        driverThreadLocal.set(driver);
        log.info("Driver created successfully on thread: {}",
                Thread.currentThread().getName());
    }

    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver not initialised for thread: "
                            + Thread.currentThread().getName()
                            + ". Call createDriver() first.");
        }
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                log.info("Driver quit on thread: {}", Thread.currentThread().getName());
            } catch (Exception e) {
                log.warn("Exception while quitting driver: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    // ── Browser builder ──────────────────────────────────────────────────────

    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // ── Stability flags (required on Linux CI) ──
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");

        // ── Headless: auto-enable on CI, or if explicitly set in config ──
        boolean isCI     = System.getenv("CI") != null;
        boolean headless = ConfigManager.getInstance().isHeadless() || isCI;
        if (headless) {
            options.addArguments("--headless=new");
            log.info("Running Chrome in headless mode (CI={})", isCI);
        }

        // ── Suppress noise ───────────────────────────────────────────────
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--blink-settings=imagesEnabled=false");
        options.setExperimentalOption("excludeSwitches",
                java.util.List.of("enable-automation", "enable-logging"));

        return new ChromeDriver(options);
    }
}