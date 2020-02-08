package springwithoutboot;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExampleTest {
    @Test
    void create_application_context() {
        final ApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        assertNotNull(context);
    }
}
