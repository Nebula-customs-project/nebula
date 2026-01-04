package pse.nebula.platformcore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "eureka.client.register-with-eureka=false",
                "eureka.client.fetch-registry=false",
                "spring.cloud.config.server.native.search-locations=classpath:/config-repo/"
        }
)
@ActiveProfiles("test")
class PlatformCoreApplicationTests {

    @Test
    void contextLoads() {
        // If this passes, Spring context loaded successfully
    }
}
