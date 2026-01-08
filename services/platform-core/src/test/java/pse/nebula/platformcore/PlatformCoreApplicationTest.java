package pse.nebula.platformcore;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ClassUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for PlatformCoreApplication context and configuration
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "eureka.client.register-with-eureka=false",
                "eureka.client.fetch-registry=false",
                "spring.cloud.config.server.native.search-locations=classpath:/config-repo/"
        }
)
@ActiveProfiles("test")
@DisplayName("Platform Core Application Tests")
class PlatformCoreApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Application context should load successfully")
    void testApplicationContext_Loads() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    @DisplayName("PlatformCoreApplication bean should exist")
    void testApplicationBean_Exists() {
        assertThat(applicationContext.containsBean("platformCoreApplication")).isTrue();
    }

    @Test
    @DisplayName("Application should have EnableEurekaServer annotation")
    void testApplication_HasEurekaServerEnabled() {
        PlatformCoreApplication application = applicationContext.getBean(PlatformCoreApplication.class);
        Class<?> userClass = ClassUtils.getUserClass(application);
        assertThat(userClass.isAnnotationPresent(EnableEurekaServer.class)).isTrue();
    }

    @Test
    @DisplayName("Application should have EnableConfigServer annotation")
    void testApplication_HasConfigServerEnabled() {
        PlatformCoreApplication application = applicationContext.getBean(PlatformCoreApplication.class);
        Class<?> userClass = ClassUtils.getUserClass(application);
        assertThat(userClass.isAnnotationPresent(EnableConfigServer.class)).isTrue();
    }

    @Test
    @DisplayName("Application should have SpringBootApplication annotation")
    void testApplication_HasSpringBootApplicationAnnotation() {
        PlatformCoreApplication application = applicationContext.getBean(PlatformCoreApplication.class);
        assertThat(application.getClass().isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class)).isTrue();
    }

    @Test
    @DisplayName("HealthController bean should exist")
    void testHealthControllerBean_Exists() {
        assertThat(applicationContext.containsBean("healthController")).isTrue();
    }

    @Test
    @DisplayName("Application should have correct number of beans")
    void testApplication_HasExpectedBeans() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        assertThat(beanNames).isNotEmpty();
        assertThat(beanNames).hasSizeGreaterThan(10); // Should have many Spring beans
    }

    @Test
    @DisplayName("Application environment should have correct active profile")
    void testApplication_HasTestProfile() {
        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        assertThat(activeProfiles).contains("test");
    }

    @Test
    @DisplayName("Application should have correct application name")
    void testApplication_HasCorrectName() {
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        assertThat(applicationName).isEqualTo("platform-core-test");
    }

    @Test
    @DisplayName("Eureka client should be disabled for this service")
    void testEurekaClient_IsDisabled() {
        Boolean registerWithEureka = applicationContext.getEnvironment()
                .getProperty("eureka.client.register-with-eureka", Boolean.class);
        assertThat(registerWithEureka).isFalse();

        Boolean fetchRegistry = applicationContext.getEnvironment()
                .getProperty("eureka.client.fetch-registry", Boolean.class);
        assertThat(fetchRegistry).isFalse();
    }

    @Test
    @DisplayName("Config server should use native profile")
    void testConfigServer_UsesNativeProfile() {
        String searchLocations = applicationContext.getEnvironment()
                .getProperty("spring.cloud.config.server.native.search-locations");
        assertThat(searchLocations).contains("config-repo");
    }
}

