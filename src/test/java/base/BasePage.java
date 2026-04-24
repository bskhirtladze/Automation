package base;

import config.ConfigManager;
import base.DriverFactory;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Base class for all Page Objects.
 * Provides driver access, explicit waits, and common interactions.
 */
public class BasePage {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    private static final ConfigManager config = ConfigManager.getInstance();

    protected BasePage() {
        this.driver = DriverFactory.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(config.getExplicitWait()));
        PageFactory.initElements(driver, this);
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    @Step("Navigate to: {url}")
    protected void navigateTo(String url) {
        log.info("Navigating to {}", url);
        driver.get(url);
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // ── Wait helpers ─────────────────────────────────────────────────────────

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected WebElement waitForClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected boolean waitForUrlContains(String fragment) {
        return wait.until(ExpectedConditions.urlContains(fragment));
    }

    protected boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // ── Interactions ─────────────────────────────────────────────────────────

    protected void click(WebElement element) {
        waitForClickable(element).click();
    }

    protected void type(WebElement element, String text) {
        waitForVisible(element);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(WebElement element) {
        return waitForVisible(element).getText().trim();
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /**
     * Attempts to click an element with a scroll-first + JS-click fallback strategy.
     * Use this instead of plain click() when ads or overlays may intercept the click.
     *
     * Strategy:
     *   1. Scroll element into the centre of the viewport
     *   2. Try a normal Selenium click
     *   3. If intercepted by an overlay, fall back to JS click
     */
    protected void safeClick(WebElement element) {
        scrollIntoView(element);
        try {
            waitForClickable(element).click();
        } catch (ElementClickInterceptedException e) {
            log.warn("Click intercepted on <{}> — falling back to JS click", element.getTagName());
            jsClick(element);
        }
    }

    /**
     * Dismisses any known ad/overlay iframes that block clicks on automationexercise.com.
     * Switches back to default content before returning.
     *
     * Call this once after page load before interacting with elements that may be covered.
     */
    protected void dismissOverlays() {
        // Known overlay selectors on automationexercise.com
        By[] overlayLocators = {
                By.id("dismiss-button-element"),
                By.cssSelector("ins.adsbygoogle iframe"),
                By.cssSelector("div[id^='google_ads']"),
                By.cssSelector("button[id*='dismiss']"),
                By.cssSelector("button[id*='close']")
        };

        for (By locator : overlayLocators) {
            try {
                WebElement overlay = driver.findElement(locator);
                if (overlay.isDisplayed()) {
                    overlay.click();
                    log.info("Dismissed overlay: {}", locator);
                }
            } catch (Exception ignored) {
                // Not present — continue
            }
        }

        // Also dismiss any top-level iframes that cover the viewport
        try {
            driver.switchTo().defaultContent();
        } catch (Exception ignored) {}
    }
}
