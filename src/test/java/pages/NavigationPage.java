package pages;


import base.BasePage;
import enums.NavigationItem;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class NavigationPage extends BasePage {


    public NavigationPage(WebDriver driver) {
        super(driver);
    }

    public void navigateTo(NavigationItem item) {
        String xpath = String.format(
                "//ul[@class='nav navbar-nav']//a[contains(@href, '%s')]",
                item.getItem()
        );
        By element = By.xpath(xpath);
        find(element).click();
    }

}
