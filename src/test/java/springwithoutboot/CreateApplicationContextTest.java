package springwithoutboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

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
    @ComponentScan(excludeFilters = @ComponentScan.Filter(value = ConfigWithTwoSelfTestingBeans.class, type = ASSIGNABLE_TYPE))
    public static class ConfigWithComponentScan {
    }

    @Component
    public static class Greeter {
        public String greeting() {
            return "Howdy!";
        }
    }

    @Test
    void inject_dependency_into_component() {
        final var context = new AnnotationConfigApplicationContext(ConfigWithComponentScan.class);

        context.getBean(InjectGreeterThroughConstructor.class).assertInitialized();
        context.getBean(InjectGreeterByMeansWhichSubvertTheTypeSystem.class).assertInitialized();
    }

    public interface SelfTestingBean {
        void assertInitialized();
    }

    @Component
    public static class InjectGreeterThroughConstructor implements SelfTestingBean {
        private final Greeter greeter;

        public InjectGreeterThroughConstructor(Greeter greeter) {
            this.greeter = greeter;
        }

        @Override
        public void assertInitialized() {
            assertNotNull(greeter);
        }
    }

    @Component
    public static class InjectGreeterByMeansWhichSubvertTheTypeSystem implements SelfTestingBean {
        @Autowired
        private Greeter greeter;

        @Override
        public void assertInitialized() {
            assertNotNull(greeter);
        }
    }

    @Test
    void resolve_ambiguity() {
        final var context = new AnnotationConfigApplicationContext(ConfigWithTwoSelfTestingBeans.class);

        final var myFirstBean = (SelfTestingBean) context.getBean("myFirstBean");
        assertNotNull(myFirstBean);
        myFirstBean.assertInitialized();

        final var mySecondBean = (SelfTestingBean) context.getBean("explicitly named bean");
        assertNotNull(mySecondBean);
        mySecondBean.assertInitialized();
    }

    @Configuration
    public static class ConfigWithTwoSelfTestingBeans {
        @Bean
        public SelfTestingBean myFirstBean() {
            return new InjectGreeterThroughConstructor(new Greeter() {
                @Override
                public String greeting() {
                    return "my first greeting";
                }
            });
        }

        @Bean(name = "explicitly named bean")
        public SelfTestingBean mySecondBean() {
            return new InjectGreeterThroughConstructor(new Greeter() {
                @Override
                public String greeting() {
                    return "my second greeting";
                }
            });
        }
    }
}
