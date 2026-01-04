package pse.nebula.worldview.infrastructure.adapter.outbound.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pse.nebula.worldview.infrastructure.adapter.outbound.persistence.entity.RouteEntity;

/**
 * Spring Data JPA repository for RouteEntity.
 */
@Repository
public interface JpaRouteRepository extends JpaRepository<RouteEntity, String> {
}

