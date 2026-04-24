package listeners;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;
import utils.RetryAnalyzer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Wires {@link RetryAnalyzer} onto every {@code @Test} method automatically,
 * so no test class needs {@code retryAnalyzer = RetryAnalyzer.class} manually.
 */
public class RetryListener implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation,
                          Class testClass,
                          Constructor testConstructor,
                          Method testMethod) {

        // Only set if the test hasn't already declared its own analyzer
        if (annotation.getRetryAnalyzerClass() == null) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}