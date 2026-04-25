package tests;

import base.BaseTest;
import enums.NavigationItem;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.NavigationPage;
import pages.TestCasePage;

@Epic("User Management")
@Feature("Test Cases Page")
public class RegisterUserTest extends BaseTest {

    @Test(description = "Navigate to Test Cases page and interact with a test case")
    @Story("User can browse test cases")
    @Severity(SeverityLevel.NORMAL)
    public void registerUser() {

        String expectedTitleText = "TEST CASES";

        // FIX 1: No-arg constructors — driver is managed internally by DriverFactory
        TestCasePage testCasePage = new TestCasePage();
        NavigationPage navigationPage = new NavigationPage();

        logStep(1, "Navigate to TEST CASES page");
        navigationPage.navigateTo(NavigationItem.CASES);

        logStep(2, "Close pop-up");
        testCasePage.closePopupFromAnyIframe(testCasePage.closePopUpLocator);

        logStep(3, "Assert title text");
        String actualTitleText = testCasePage.getTitle();
        Assert.assertEquals(actualTitleText, expectedTitleText,
                "Title text should be " + expectedTitleText);

        logStep(4, "Click test case number");
        testCasePage.clickTestCaseByIndex(2);

        // FIX 2: Use the inherited SLF4J logger — not org.testng.internal.Utils.log
        log.info("Wow!");
    }

    @Test(description = "User work")
    @Story("User can work")
    @Severity(SeverityLevel.CRITICAL)
    public void userWorkTest() {
        log.info("User is working");
    }

    @Test(description = "User on hold")
    @Story("User hold")
    @Severity(SeverityLevel.NORMAL)
    public void userOnHoldTest() {
        log.info("User is on hold");
    }

    @Test(description = "User stack")
    @Story("User stack")
    @Severity(SeverityLevel.MINOR)
    public void userStackTest() {
        Assert.assertFalse(true, "This test should fail");
        log.error("User stack");
    }

    @Test(description = "User log out")
    @Story("User can log out")
    @Severity(SeverityLevel.CRITICAL)
    public void logOutUserTest() {
        log.info("User log out");
    }
}
