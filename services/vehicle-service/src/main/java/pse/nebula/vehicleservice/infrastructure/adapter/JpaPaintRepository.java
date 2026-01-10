package pse.nebula.vehicleservice.infrastructure.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pse.nebula.vehicleservice.domain.model.CarType;
import pse.nebula.vehicleservice.domain.model.Paint;
import pse.nebula.vehicleservice.domain.port.PaintRepository;

import java.util.List;

/**
 * JPA implementation of PaintRepository.
 */
@Repository
public interface JpaPaintRepository extends JpaRepository<Paint, Integer>, PaintRepository {

    /**
     * Find all paints that have a price for the given car type.
     * Eagerly fetches the prices collection to avoid lazy loading issues.
     */
    @Query("SELECT DISTINCT p FROM Paint p " +
           "JOIN FETCH p.prices " +
           "WHERE EXISTS (SELECT 1 FROM PaintPrice pp WHERE pp.paint = p AND pp.carType = :carType)")
    List<Paint> findAllWithPricesForCarType(@Param("carType") CarType carType);
}

