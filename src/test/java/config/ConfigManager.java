package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton configuration manager.
 * Reads values from config.properties; any key can be
 * overridden at runtime with a JVM system property
 * (e.g. -Dbrowser=firefox).
 */
public final class ConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);
    private static final String CONFIG_FILE = "config.properties";

    private static ConfigManager instance;
    private final Properties props = new Properties();

    private ConfigManager() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                throw new RuntimeException("config.properties not found on classpath");
            }
            props.load(is);
            log.info("Configuration loaded from {}", CONFIG_FILE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + CONFIG_FILE, e);
        }
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    /** Returns the value; JVM system property wins over config file. */
    public String get(String key) {
        return System.getProperty(key, props.getProperty(key));
    }

    public String get(String key, String defaultValue) {
        String value = get(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key));
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
    }

    // ── Convenience getters ───────────────────────────────────────────────────

    public String getBrowser()       { return get("browser", "chrome"); }
    public boolean isHeadless()      { return getBoolean("headless", false); }
    public String getBaseUrl()       { return get("base.url"); }
    public int getImplicitWait()     { return getInt("implicit.wait", 10); }
    public int getExplicitWait()     { return getInt("explicit.wait", 15); }
    public int getPageLoadTimeout()  { return getInt("page.load.timeout", 30); }
    public boolean screenshotOnFailure() { return getBoolean("screenshot.on.failure", true); }
    public String getScreenshotDir() { return get("screenshot.dir", "target/screenshots"); }
    public int getRetryCount()       { return getInt("retry.count", 1); }
}