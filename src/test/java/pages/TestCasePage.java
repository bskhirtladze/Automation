package pages;

import base.BasePage;
import base.ElementHighlighter;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class TestCasePage extends BasePage {

    private By titleTextLocator = By.cssSelector("#form h2 > b");
    public By closePopUpLocator = By.id("dismiss-button-element");
    private By testCaseNumbers = By.cssSelector("a[href*='collapse']");

    public TestCasePage(WebDriver driver) {
        super(driver);
    }

    @Step
    public String getTitle() {
        log.info("Get text from title");
        return find(titleTextLocator).getText().trim();
    }

    @Step
    public void clickTestCaseByIndex(int index) {
        index = index - 1;
        List<WebElement> elements = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(testCaseNumbers)
        );

        if (index < 0 || index >= elements.size()) {
            throw new IndexOutOfBoundsException(
                    "Invalid index: " + index + ", elements found: " + elements.size()
            );
        }

        WebElement element = elements.get(index);

        ElementHighlighter.highlight(driver, element);

        try {
            log.info("Clicked " + index + " test case");
            element.click();
        } catch (ElementClickInterceptedException e) {
            js.executeScript("arguments[0].click();", element);
        }
    }
}
