package base;

import config.ConfigManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Thread-local WebDriver factory.
 * Each thread gets its own driver instance — safe for parallel test execution.
 */
public final class DriverFactory {

    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ConfigManager config = ConfigManager.getInstance();

    private DriverFactory() {}

    /** Creates and stores a WebDriver for the current thread. */
    public static void createDriver() {
        String browser = config.getBrowser().toLowerCase();
        boolean headless = config.isHeadless();
        log.info("Initialising {} driver (headless={})", browser, headless);

        WebDriver driver = switch (browser) {
            case "firefox" -> createFirefox(headless);
            case "edge"    -> createEdge(headless);
            default        -> createChrome(headless);
        };

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
        driver.manage().window().maximize();

        driverThreadLocal.set(driver);
        log.info("Driver created successfully for thread {}", Thread.currentThread().getName());
    }

    /** Returns the WebDriver for the current thread. */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver is null — call DriverFactory.createDriver() before getDriver()");
        }
        return driver;
    }

    /** Quits the driver and removes it from the thread-local store. */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
            log.info("Driver quit for thread {}", Thread.currentThread().getName());
        }
    }

    // ── Browser builders ─────────────────────────────────────────────────────

    private static WebDriver createChrome(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");
        if (headless) options.addArguments("--headless=new");
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefox(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        if (headless) options.addArguments("--headless");
        return new FirefoxDriver(options);
    }

    private static WebDriver createEdge(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        if (headless) options.addArguments("--headless=new");
        return new EdgeDriver(options);
    }
}