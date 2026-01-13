package pse.nebula.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pse.nebula.user.model.Role;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for User entity.
 * Excludes sensitive information like password from API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

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
}
