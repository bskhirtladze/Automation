package pages;

import base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object for the Login / Register page.
 */
public class AuthPage extends BasePage {

    // ── Signup section ────────────────────────────────────────────────────────

    @FindBy(css = "div.signup-form h2")
    private WebElement signupHeading;

    @FindBy(css = "input[data-qa='signup-name']")
    private WebElement signupNameField;

    @FindBy(css = "input[data-qa='signup-email']")
    private WebElement signupEmailField;

    @FindBy(css = "button[data-qa='signup-button']")
    private WebElement signupButton;

    // ── Login section ─────────────────────────────────────────────────────────

    @FindBy(css = "div.login-form h2")
    private WebElement loginHeading;

    @FindBy(css = "input[data-qa='login-email']")
    private WebElement loginEmailField;

    @FindBy(css = "input[data-qa='login-password']")
    private WebElement loginPasswordField;

    @FindBy(css = "button[data-qa='login-button']")
    private WebElement loginButton;

    @FindBy(css = "p.text-danger")
    private WebElement errorMessage;

    public AuthPage() {
        super();
    }

    @Step("Verify 'New User Signup!' heading is visible")
    public boolean isSignupSectionVisible() {
        return waitForVisible(signupHeading).isDisplayed();
    }

    @Step("Enter signup name: {name}")
    public AuthPage enterSignupName(String name) {
        type(signupNameField, name);
        return this;
    }

    @Step("Enter signup email: {email}")
    public AuthPage enterSignupEmail(String email) {
        type(signupEmailField, email);
        return this;
    }

    @Step("Click 'Signup' button")
    public RegisterPage clickSignup() {
        click(signupButton);
        return new RegisterPage();
    }

    @Step("Enter login email: {email}")
    public AuthPage enterLoginEmail(String email) {
        type(loginEmailField, email);
        return this;
    }

    @Step("Enter login password")
    public AuthPage enterLoginPassword(String password) {
        type(loginPasswordField, password);
        return this;
    }

    @Step("Click 'Login' button")
    public AuthPage clickLogin() {
        click(loginButton);
        return this;
    }

    @Step("Get error message text")
    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public boolean isErrorMessageDisplayed() {
        return isElementPresent(org.openqa.selenium.By.cssSelector("p.text-danger"));
    }
}