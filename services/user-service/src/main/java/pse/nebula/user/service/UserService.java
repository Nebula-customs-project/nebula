package pse.nebula.user.service;

import pse.nebula.user.dto.AuthenticationResult;
import pse.nebula.user.model.User;
import pse.nebula.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil,
            BCryptPasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {
        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User and user ID must not be null for update");
        }
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User with id " + user.getId() + " not found"));

        // Handle password updates securely
        String newPassword = user.getPassword();
        if (newPassword != null && !newPassword.isBlank()) {
            // A new password was provided; always hash and update it
            user.setPassword(passwordEncoder.encode(newPassword));
        } else {
            // No new password provided; retain existing hashed password
            user.setPassword(existingUser.getPassword());
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Authenticate user and return JWT access token + refresh token with user
     * object.
     */
    public AuthenticationResult authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String userId = user.getId().toString();
        String role = user.getRole().name();

        // Generate JWT access token
        String accessToken = jwtUtil.generateToken(userId, user.getEmail(), role);

        // Generate refresh token
        RefreshTokenService.RefreshTokenData refreshData = refreshTokenService.generateRefreshToken(userId,
                user.getEmail(), role);

        return new AuthenticationResult(
                accessToken,
                refreshData.token(),
                jwtUtil.getAccessTokenTtlSeconds(),
                refreshData.ttlSeconds(),
                user);
    }

    /**
     * Revoke all refresh tokens for a user (used during logout).
     */
    public void revokeUserRefreshTokens(String userId) {
        refreshTokenService.revokeAllForUser(userId);
    }
}