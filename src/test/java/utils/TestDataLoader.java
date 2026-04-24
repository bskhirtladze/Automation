package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Generic JSON test-data loader.
 *
 * <pre>{@code
 * UserData user = TestDataLoader.load("testdata/user.json", UserData.class);
 * }</pre>
 */
public final class TestDataLoader {

    private static final Logger log = LoggerFactory.getLogger(TestDataLoader.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TestDataLoader() {}

    /**
     * Loads a JSON file from the test classpath and maps it to the given type.
     *
     * @param resourcePath classpath-relative path, e.g. {@code "testdata/user.json"}
     * @param type         target POJO class
     */
    public static <T> T load(String resourcePath, Class<T> type) {
        try (InputStream is = TestDataLoader.class.getClassLoader()
                .getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("Test data file not found on classpath: " + resourcePath);
            }
            T data = MAPPER.readValue(is, type);
            log.info("Loaded test data from {} as {}", resourcePath, type.getSimpleName());
            return data;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test data from " + resourcePath, e);
        }
    }
}