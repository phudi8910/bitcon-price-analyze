package myapp.bitcoin_analyze;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import myapp.bitcoin_analyze.BitcoinAnalyzeApplication;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = BitcoinAnalyzeApplication.class)
public @interface IntegrationTest {
}
