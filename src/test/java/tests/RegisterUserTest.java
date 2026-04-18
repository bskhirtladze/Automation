package tests;

import base.BaseTest;
import enums.NavigationItem;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.NavigationPage;
import pages.TestCasePage;

public class RegisterUserTest extends BaseTest {

    @Test
    public void registerUser() {

        String expectedTitleText = "TEST CASES";
        String actualTitleText;

        TestCasePage testCasePage = new TestCasePage(driver);
        NavigationPage navigationPage = new NavigationPage(driver);

        logStep(1, "Navigate to TEST CASES page");
        navigationPage.navigateTo(NavigationItem.CASES);

        logStep(2, "Close pop-up");
        testCasePage.closePopupFromAnyIframe(testCasePage.closePopUpLocator);

        logStep(3, "Assert title text");
        actualTitleText = testCasePage.getTitle();
        Assert.assertEquals(actualTitleText, expectedTitleText, "Title text should be " + expectedTitleText);

        logStep(4, "Click test case number");
        testCasePage.clickTestCaseByIndex(2);
        System.out.println("Wow!");

    }
}
