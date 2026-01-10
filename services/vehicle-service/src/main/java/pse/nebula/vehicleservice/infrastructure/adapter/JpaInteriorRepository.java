package pse.nebula.vehicleservice.infrastructure.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pse.nebula.vehicleservice.domain.model.CarType;
import pse.nebula.vehicleservice.domain.model.Interior;
import pse.nebula.vehicleservice.domain.port.InteriorRepository;

import java.util.List;

/**
 * JPA implementation of InteriorRepository.
 */
@Repository
public interface JpaInteriorRepository extends JpaRepository<Interior, Integer>, InteriorRepository {

    /**
     * Find all interiors that have a price for the given car type.
     * Eagerly fetches the prices collection to avoid lazy loading issues.
     */
    @Query("SELECT DISTINCT i FROM Interior i " +
           "JOIN FETCH i.prices " +
           "WHERE EXISTS (SELECT 1 FROM InteriorPrice ip WHERE ip.interior = i AND ip.carType = :carType)")
    List<Interior> findAllWithPricesForCarType(@Param("carType") CarType carType);
}

