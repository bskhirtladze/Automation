package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.ElementHighlighter;

public class CustomElement {
    private WebDriver driver;
    private WebElement element;

    public CustomElement(WebDriver driver, WebElement element) {
        this.driver = driver;
        this.element = element;
    }

    public void click() {
        ElementHighlighter.highlight(driver, element);
        element.click();
    }

    public void sendKeys(String text) {
        ElementHighlighter.highlight(driver, element);
        element.sendKeys(text);
    }

    public String getText() {
        ElementHighlighter.highlight(driver, element);
        return element.getText();
    }

    public WebElement get() {
        return element;
    }
}
