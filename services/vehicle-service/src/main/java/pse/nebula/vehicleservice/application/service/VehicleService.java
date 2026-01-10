package pse.nebula.vehicleservice.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pse.nebula.vehicleservice.domain.exception.VehicleNotFoundException;
import pse.nebula.vehicleservice.domain.model.Vehicle;
import pse.nebula.vehicleservice.domain.port.VehicleRepository;

import java.util.List;

/**
 * Service for vehicle-related operations.
 * Handles the Cars Overview functionality.
 */
@Service
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Get all vehicles for the Cars Overview page.
     *
     * @return list of all vehicles
     */
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    /**
     * Get a specific vehicle by ID.
     *
     * @param vehicleId the vehicle ID
     * @return the vehicle
     * @throws VehicleNotFoundException if vehicle not found
     */
    public Vehicle getVehicleById(Integer vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
    }
}

