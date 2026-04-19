package utils;

public class TestContext {
    private static ThreadLocal<String> testName = new ThreadLocal<>();

    public static void setTestName(String name) {
        testName.set(name);
    }

    public static String getTestName() {
        return testName.get();
    }

    public static void clear() {
        testName.remove();
    }
}
