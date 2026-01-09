package pse.nebula.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "security")
public class PublicRoutesConfig {

    private List<String> publicRoutes = new ArrayList<>();

    public List<String> getPublicRoutes() {
        return publicRoutes;
    }

    public void setPublicRoutes(List<String> publicRoutes) {
        this.publicRoutes = publicRoutes;
    }

    public boolean isPublicRoute(String path) {
        return publicRoutes.stream()
                .anyMatch(pattern -> pathMatches(path, pattern));
    }

    private boolean pathMatches(String path, String pattern) {
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        }
        if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            // Path must start with prefix + '/' and have no additional '/' after that
            if (!path.startsWith(prefix + "/")) {
                return false;
            }
            String remaining = path.substring(prefix.length() + 1);
            return remaining.length() > 0 && !remaining.contains("/");
        }
        return path.equals(pattern);
    }
}
