package pse.nebula.user.repository;

import pse.nebula.user.model.UserVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserVehicleRepository extends JpaRepository<UserVehicle, Long> {

    Optional<UserVehicle> findByVehicleId(String vehicleId);
}