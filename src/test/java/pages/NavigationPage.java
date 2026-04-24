package pages;

import base.BasePage;
import enums.NavigationItem;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class NavigationPage extends BasePage {

    public NavigationPage() {
        super();
    }

    @Step("Navigate to: {item}")
    public void navigateTo(NavigationItem item) {
        By navLink = By.xpath(String.format(
                "//ul[@class='nav navbar-nav']//a[contains(@href, '%s')]",
                item.getItem()
        ));

        log.info("Navigating to {} ({})", item.name(), item.getItem());

        // 1. Dismiss any ad overlays that would intercept the click
        dismissOverlays();

        // 2. Wait for the link to be visible, scroll it into view, then click
        //    safeClick() handles ElementClickInterceptedException with a JS fallback
        WebElement link = waitForVisible(navLink);
        safeClick(link);
    }
}

