package pse.nebula.user.controller;

import pse.nebula.user.model.User;
import pse.nebula.user.model.UserVehicle;
import pse.nebula.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        if (!userService.getUserById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        user.setId(id);
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userService.getUserById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Vehicle endpoints for MyPSECar
    @GetMapping("/{userId}/vehicle")
    public ResponseEntity<UserVehicle> getUserVehicle(@PathVariable Long userId) {
        return userService.getUserById(userId)
                .flatMap(user -> userService.getVehicleById(user.getVehicleId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/vehicle")
    public ResponseEntity<UserVehicle> createUserVehicle(@PathVariable Long userId, @Valid @RequestBody UserVehicle vehicle) {
        return userService.getUserById(userId)
                .map(user -> {
                    vehicle.setOwner(user);
                    return ResponseEntity.ok(userService.createVehicle(vehicle));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}