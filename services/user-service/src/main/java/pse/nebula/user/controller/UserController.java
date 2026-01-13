package pse.nebula.user.controller;

import pse.nebula.user.model.User;
import pse.nebula.user.service.UserService;
import pse.nebula.user.service.TokenBlacklistService;
import pse.nebula.user.dto.LoginRequest;
import pse.nebula.user.dto.LoginResponse;
import pse.nebula.user.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    /**
     * Register a new user
     * POST /users/register
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromUser(createdUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Login user and get JWT token
     * POST /users/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            String token = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            User user = userService.getUserByEmail(loginRequest.getEmail()).get();

            LoginResponse response = new LoginResponse();
            response.setUserId(user.getId());
            response.setEmail(user.getEmail());
            response.setUsername(user.getUsername());
            response.setToken(token);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Logout user and blacklist token
     * POST /users/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logoutUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            // Extract token from "Bearer <token>" header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Missing or invalid Authorization header");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix

            // Add token to blacklist
            tokenBlacklistService.blacklistToken(token);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Logged out successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Logout failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(UserResponse::fromUser)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(UserResponse::fromUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(UserResponse::fromUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        if (!userService.getUserById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        user.setId(id);
        return ResponseEntity.ok(UserResponse.fromUser(userService.updateUser(user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userService.getUserById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}