package pages;

import base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object for the account registration / account information form.
 */
public class RegisterPage extends BasePage {

    @FindBy(css = "b")
    private WebElement enterAccountInfoHeading;

    // ── Personal info ─────────────────────────────────────────────────────────

    @FindBy(css = "input#id_gender1")
    private WebElement genderMrRadio;

    @FindBy(css = "input#id_gender2")
    private WebElement genderMrsRadio;

    @FindBy(css = "input#password")
    private WebElement passwordField;

    @FindBy(css = "select#days")
    private WebElement dobDaySelect;

    @FindBy(css = "select#months")
    private WebElement dobMonthSelect;

    @FindBy(css = "select#years")
    private WebElement dobYearSelect;

    // ── Newsletter / offers ───────────────────────────────────────────────────

    @FindBy(css = "input#newsletter")
    private WebElement newsletterCheckbox;

    @FindBy(css = "input#optin")
    private WebElement offersCheckbox;

    // ── Address ───────────────────────────────────────────────────────────────

    @FindBy(css = "input#first_name")
    private WebElement firstNameField;

    @FindBy(css = "input#last_name")
    private WebElement lastNameField;

    @FindBy(css = "input#company")
    private WebElement companyField;

    @FindBy(css = "input#address1")
    private WebElement address1Field;

    @FindBy(css = "input#address2")
    private WebElement address2Field;

    @FindBy(css = "select#country")
    private WebElement countrySelect;

    @FindBy(css = "input#state")
    private WebElement stateField;

    @FindBy(css = "input#city")
    private WebElement cityField;

    @FindBy(css = "input#zipcode")
    private WebElement zipcodeField;

    @FindBy(css = "input#mobile_number")
    private WebElement mobileField;

    // ── Submit ────────────────────────────────────────────────────────────────

    @FindBy(css = "button[data-qa='create-account']")
    private WebElement createAccountButton;

    // ── Success ───────────────────────────────────────────────────────────────

    @FindBy(css = "h2[data-qa='account-created'] b")
    private WebElement accountCreatedHeading;

    @FindBy(css = "a[data-qa='continue-button']")
    private WebElement continueButton;

    public RegisterPage() {
        super();
    }

    @Step("Verify 'Enter Account Information' form is displayed")
    public boolean isLoaded() {
        return waitForVisible(enterAccountInfoHeading).isDisplayed();
    }

    @Step("Select title: {title}")
    public RegisterPage selectTitle(String title) {
        if ("Mr".equalsIgnoreCase(title)) {
            jsClick(genderMrRadio);
        } else {
            jsClick(genderMrsRadio);
        }
        return this;
    }

    @Step("Enter password")
    public RegisterPage enterPassword(String password) {
        type(passwordField, password);
        return this;
    }

    @Step("Select date of birth: day={day}, month={month}, year={year}")
    public RegisterPage selectDob(String day, String month, String year) {
        new Select(dobDaySelect).selectByValue(day);
        new Select(dobMonthSelect).selectByValue(month);
        new Select(dobYearSelect).selectByValue(year);
        return this;
    }

    @Step("Subscribe to newsletter: {subscribe}")
    public RegisterPage subscribeNewsletter(boolean subscribe) {
        if (subscribe && !newsletterCheckbox.isSelected()) jsClick(newsletterCheckbox);
        return this;
    }

    @Step("Opt in to special offers: {optIn}")
    public RegisterPage optInOffers(boolean optIn) {
        if (optIn && !offersCheckbox.isSelected()) jsClick(offersCheckbox);
        return this;
    }

    @Step("Fill address: firstName={firstName}, lastName={lastName}")
    public RegisterPage fillAddress(String firstName, String lastName,
                                    String company, String address1,
                                    String address2, String country,
                                    String state, String city,
                                    String zipcode, String mobile) {
        type(firstNameField, firstName);
        type(lastNameField, lastName);
        type(companyField, company);
        type(address1Field, address1);
        type(address2Field, address2);
        new Select(countrySelect).selectByVisibleText(country);
        type(stateField, state);
        type(cityField, city);
        type(zipcodeField, zipcode);
        type(mobileField, mobile);
        return this;
    }

    @Step("Click 'Create Account' button")
    public RegisterPage clickCreateAccount() {
        scrollIntoView(createAccountButton);
        click(createAccountButton);
        return this;
    }

    @Step("Verify 'ACCOUNT CREATED!' message is displayed")
    public boolean isAccountCreated() {
        return waitForVisible(accountCreatedHeading).isDisplayed();
    }

    @Step("Click 'Continue' after account creation")
    public HomePage clickContinue() {
        click(continueButton);
        return new HomePage();
    }
}