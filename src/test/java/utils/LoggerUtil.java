package utils;

import io.qameta.allure.Allure;

public class LoggerUtil {
    public static void log(String message) {
        String testName = TestContext.getTestName();

        System.out.println("[" + testName + "] " + message);

        Allure.step(message);
    }
}
