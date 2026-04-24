package base;

import config.ConfigManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Thread-safe WebDriver factory using ThreadLocal.
 * Supports chrome and firefox; reads config from ConfigManager.
 */
public class DriverFactory {

    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverFactory() {}

    public static void createDriver() {
        String browser = ConfigManager.getInstance().getBrowser().toLowerCase().trim();
        log.info("Creating '{}' driver (headless={})", browser,
                ConfigManager.getInstance().isHeadless());

        WebDriver driver = switch (browser) {
            case "firefox" -> createFirefoxDriver();
            default        -> createChromeDriver();   // chrome is the default
        };

        // Timeouts
        int implicitWait   = ConfigManager.getInstance().getImplicitWait();
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

    // ── Browser builders ─────────────────────────────────────────────────────

    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // ── Stability flags (required on Linux CI) ──
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");

        // ── Headless ────────────────────────────────
        if (ConfigManager.getInstance().isHeadless()) {
            options.addArguments("--headless=new");  // "new" headless avoids Chrome 112+ issues
        }

        // ── Suppress noise ──────────────────────────
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--blink-settings=imagesEnabled=false"); // faster page loads
        options.setExperimentalOption("excludeSwitches",
                java.util.List.of("enable-automation", "enable-logging"));

        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions();

        if (ConfigManager.getInstance().isHeadless()) {
            options.addArguments("--headless");
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
        }

        return new FirefoxDriver(options);
    }
}