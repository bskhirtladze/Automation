package pages;

import base.BasePage;
import config.ConfigManager;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object for the Automation Exercise home page.
 */
public class HomePage extends BasePage {

    private static final ConfigManager config = ConfigManager.getInstance();

    @FindBy(css = "a[href='/login']")
    private WebElement signupLoginButton;

    @FindBy(css = "a[href='/']")
    private WebElement homeLink;

    @FindBy(css = "div.slider-feature")
    private WebElement heroSlider;

    public HomePage() {
        super();
    }

    @Step("Open home page")
    public HomePage open() {
        navigateTo(config.getBaseUrl());
        log.info("Home page opened");
        return this;
    }

    @Step("Click 'Signup / Login' button")
    public AuthPage clickSignupLogin() {
        log.info("Clicking Signup/Login");
        click(signupLoginButton);
        return new AuthPage();
    }

    @Step("Verify home page is displayed")
    public boolean isLoaded() {
        return isElementPresent(org.openqa.selenium.By.cssSelector("div.slider-feature"))
                || getPageTitle().contains("Automation Exercise");
    }
}