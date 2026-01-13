package pse.nebula.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pse.nebula.user.dto.UpdateRoleRequest;
import pse.nebula.user.dto.UserResponse;
import pse.nebula.user.model.Role;
import pse.nebula.user.model.User;
import pse.nebula.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    /**
     * Get all users - Admin only
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers().stream()
                .map(UserResponse::fromUser)
                .toList();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID - Admin only
     */
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(UserResponse::fromUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete user - Admin only
     */
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        if (userService.getUserById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    /**
     * Update user role - Admin only
     */
    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Long id,
            @RequestBody UpdateRoleRequest request) {
        
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(request.getRole());
        User updatedUser = userService.updateUser(user);
        
        return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
    }
}
