package base;


import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.Duration;
import java.util.List;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor js;
    protected Logger log = LogManager.getLogger(this.getClass());

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.driver = DriverFactory.getDriver();
        this.js = (JavascriptExecutor) driver;
        this.wait = new WebDriverWait(this.driver, Duration.ofSeconds(20));
    }

    @Step
    protected void click(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
        } catch (ElementClickInterceptedException e) {
            log.info("Normal click failed, using JS click...");
            WebElement element = driver.findElement(locator);
            js.executeScript("arguments[0].click();", element);
        }
    }

    @Step
    public CustomElement find(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return new CustomElement(driver, element);
    }

    @Step
    public void closePopupFromAnyIframe(By closeBtnLocator) {
        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));

        for (WebElement frame : iframes) {
            try {
                driver.switchTo().frame(frame);

                List<WebElement> buttons = driver.findElements(closeBtnLocator);
                if (!buttons.isEmpty()) {
                    buttons.get(0).click();
                    log.info("Popup closed inside iframe");
                    break;
                }

            } catch (Exception ignored) {
            } finally {
                driver.switchTo().defaultContent();
            }
        }
    }

}
