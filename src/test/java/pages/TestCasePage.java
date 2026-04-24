package pages;

import base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.ElementHighlighter;

import java.util.List;

public class TestCasePage extends BasePage {

    private final By titleTextLocator  = By.cssSelector("#form h2 > b");
    public  final By closePopUpLocator = By.id("dismiss-button-element");
    private final By testCaseNumbers   = By.cssSelector("a[href*='collapse']");

    // FIX 1: No-arg constructor — driver is provided by DriverFactory inside BasePage
    public TestCasePage() {
        super();
    }

    @Step("Get page title text")
    public String getTitle() {
        log.info("Getting text from title");
        // FIX 2: find() does not exist in BasePage — use waitForVisible(By)
        return waitForVisible(titleTextLocator).getText().trim();
    }

    @Step("Click test case at index {index}")
    public void clickTestCaseByIndex(int index) {
        // Convert 1-based input to 0-based list index
        int zeroBasedIndex = index - 1;

        List<WebElement> elements = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(testCaseNumbers)
        );

        if (zeroBasedIndex < 0 || zeroBasedIndex >= elements.size()) {
            throw new IndexOutOfBoundsException(
                    "Invalid index: " + index + " (0-based: " + zeroBasedIndex
                            + "), total elements found: " + elements.size()
            );
        }

        WebElement element = elements.get(zeroBasedIndex);

        // FIX 3: driver is the protected field inherited from BasePage — no redeclaration needed
        ElementHighlighter.highlight(driver, element);

        try {
            // FIX 4: Use SLF4J placeholder syntax instead of string concatenation
            log.info("Clicking test case at index {}", index);
            element.click();
        } catch (ElementClickInterceptedException e) {
            log.warn("Direct click intercepted for index {}, falling back to JS click", index);
            // FIX 5: js field doesn't exist — use the inherited jsClick() from BasePage
            jsClick(element);
        }
    }

    /**
     * Closes a pop-up that may be inside any iframe on the page.
     * Iterates through all iframes, switching into each to search for the dismiss button.
     * Switches back to the default content regardless of outcome.
     *
     * @param dismissLocator the By locator for the close/dismiss button
     */
    @Step("Close pop-up from any iframe")
    public void closePopupFromAnyIframe(By dismissLocator) {
        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
        log.info("Found {} iframe(s) on page — searching for pop-up dismiss button", iframes.size());

        for (int i = 0; i < iframes.size(); i++) {
            try {
                driver.switchTo().frame(i);
                if (isElementPresent(dismissLocator)) {
                    // Click BEFORE switching back — button lives inside this iframe
                    waitForClickable(dismissLocator).click();
                    log.info("Pop-up dismissed inside iframe at index {}", i);
                    driver.switchTo().defaultContent();
                    return;
                }
                // Button not in this iframe — return to default and try the next one
                driver.switchTo().defaultContent();
            } catch (Exception e) {
                log.warn("Could not interact with iframe {}: {}", i, e.getMessage());
                driver.switchTo().defaultContent();
            }
        }

        // Pop-up may also be in the main document (outside any iframe)
        if (isElementPresent(dismissLocator)) {
            waitForClickable(dismissLocator).click();
            log.info("Pop-up dismissed in main document");
        } else {
            log.info("No pop-up dismiss button found — skipping");
        }
    }
}
