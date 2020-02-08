package springwithoutboot;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExampleTest {
    @Test
    void create_application_context() {
        final ApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        assertNotNull(context);
    }

    @Test
    void create_application_context_with_bean() {
        final ApplicationContext context = new AnnotationConfigApplicationContext(ConfigWithBean.class);

        final var stringBean = context.getBean(String.class);

        assertEquals("Hello, World!", stringBean);
    }

    @Configuration
    public static class ConfigWithBean {
        @Bean
        public String aString() {
            return "Hello, World!";
        }
    }
}
