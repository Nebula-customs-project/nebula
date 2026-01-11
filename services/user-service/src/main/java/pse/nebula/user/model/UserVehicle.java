package pse.nebula.user.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "user_vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String vehicleId; // Matches MQTT topic or external ID

    private String model; // e.g., "Supercar"
    private String color;
    private String rims;
    private String interiorColor;

    // Live data from MQTT
    private Double fuelLevel; // Percentage
    private Double latitude;
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
}