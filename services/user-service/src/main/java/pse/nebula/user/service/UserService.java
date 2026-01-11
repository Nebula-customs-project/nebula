package pse.nebula.user.service;

import pse.nebula.user.model.User;
import pse.nebula.user.model.UserVehicle;
import pse.nebula.user.repository.UserRepository;
import pse.nebula.user.repository.UserVehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserVehicleRepository vehicleRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Vehicle methods
    public List<UserVehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<UserVehicle> getVehicleById(String vehicleId) {
        return vehicleRepository.findByVehicleId(vehicleId);
    }

    public UserVehicle createVehicle(UserVehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public UserVehicle updateVehicle(UserVehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }
}