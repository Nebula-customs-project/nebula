package pse.nebula.user.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import pse.nebula.user.model.Role;
import pse.nebula.user.model.User;
import pse.nebula.user.repository.UserRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        // Check if any admin user exists
        Optional<User> existingAdmin = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .findFirst();

        if (existingAdmin.isEmpty()) {
            log.info("No admin user found. Creating default admin user...");
            
            User admin = new User();
            admin.setUsername("admin");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@nebula.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // Default password
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);
            
            log.info("✅ Default admin user created successfully!");
            log.info("   Email: admin@nebula.com");
            log.info("   Password: admin123");
            log.info("   ⚠️  PLEASE CHANGE THE PASSWORD AFTER FIRST LOGIN!");
        } else {
            log.info("Admin user already exists. Skipping initialization.");
        }
    }
}
