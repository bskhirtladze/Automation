package utils;

import config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed test up to {@code retry.count} times (from config.properties).
 * A fresh instance is created per test method by TestNG.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(RetryAnalyzer.class);

    private int attempt   = 0;
    private final int max = ConfigManager.getInstance().getRetryCount();

    @Override
    public boolean retry(ITestResult result) {
        if (attempt < max) {
            attempt++;
            log.warn("🔁 Retrying '{}' — attempt {}/{}", result.getName(), attempt, max);
            return true;
        }
        log.error("💀 Giving up on '{}' after {} attempt(s)", result.getName(), max);
        return false;
    }
}