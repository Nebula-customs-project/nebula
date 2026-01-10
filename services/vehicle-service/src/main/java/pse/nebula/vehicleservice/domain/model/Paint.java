package pse.nebula.vehicleservice.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a paint/exterior color option.
 */
@Getter
@Entity
@Table(name = "paint", schema = "vehicle_service")
public class Paint {

    // Getters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(mappedBy = "paint", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PaintPrice> prices = new ArrayList<>();

    // Default constructor for JPA
    protected Paint() {
    }

    public Paint(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Helper method to add price
    public void addPrice(PaintPrice price) {
        prices.add(price);
        price.setPaint(this);
    }
}

