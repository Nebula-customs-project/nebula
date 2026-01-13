package pse.nebula.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pse.nebula.user.model.Role;
import pse.nebula.user.model.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String profileImage;
    private String country;
    private String city;
    private Role role;
    private LocalDateTime registrationDate;

    /**
     * Create a UserResponse from a User entity, excluding the password field
     * @param user the User entity to convert
     * @return a UserResponse with all fields except the password, or null if the user is null
     */
    public static UserResponse fromUser(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setProfileImage(user.getProfileImage());
        response.setCountry(user.getCountry());
        response.setCity(user.getCity());
        response.setRole(user.getRole());
        response.setRegistrationDate(user.getRegistrationDate());
        
        return response;
    }
}
