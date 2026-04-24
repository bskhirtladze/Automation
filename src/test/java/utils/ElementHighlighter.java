package utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Temporarily highlights a WebElement with a colored border via JavaScript.
 * Useful for visual debugging during test development.
 *
 * Usage:
 *   ElementHighlighter.highlight(driver, element);
 */
public final class ElementHighlighter {

    private static final Logger log = LoggerFactory.getLogger(ElementHighlighter.class);

    private static final String HIGHLIGHT_STYLE  = "border: 3px solid red; background: yellow;";
    private static final String ORIGINAL_RESTORE  = "";
    private static final int    HIGHLIGHT_DURATION_MS = 300;

    private ElementHighlighter() {}

    /**
     * Highlights the element briefly, then restores its original style.
     *
     * @param driver  active WebDriver instance
     * @param element element to highlight
     */
    public static void highlight(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String originalStyle = element.getAttribute("style");

        try {
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);",
                    element, HIGHLIGHT_STYLE);
            Thread.sleep(HIGHLIGHT_DURATION_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Highlight sleep interrupted: {}", e.getMessage());
        } finally {
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);",
                    element, originalStyle != null ? originalStyle : ORIGINAL_RESTORE);
        }
    }
}
