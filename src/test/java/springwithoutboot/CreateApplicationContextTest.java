package springwithoutboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.*;

public class CreateApplicationContextTest {
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

    @Test
    void given_config_with_non_uniquely_defined_beans_When_getting_bean_Then_it_fails() {
        final var context = new AnnotationConfigApplicationContext(ConfigWithNonUniqueBeans.class);
        assertThrows(
            NoUniqueBeanDefinitionException.class,
            () -> context.getBean(String.class)
        );
    }

    @Configuration
    public static class ConfigWithNonUniqueBeans {
        @Bean
        public String aString() {
            return "Hello, World!";
        }

        @Bean
        public String anotherString() {
            return "Now I have two String beans";
        }
    }

    @Test
    void component_scan() {
        final var context = new AnnotationConfigApplicationContext(ConfigWithComponentScan.class);

        final var greeter = context.getBean(Greeter.class);

        assertNotNull(greeter);
        assertEquals("Howdy!", greeter.greeting());
    }

    @Configuration
    @ComponentScan
    public static class ConfigWithComponentScan {
    }

    @Component
    public static class Greeter {
        public String greeting() {
            return "Howdy!";
        }
    }
}
