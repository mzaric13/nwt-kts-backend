package nwt.kts.backend.repository;

import nwt.kts.backend.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Integer> {
    Route findRouteById(int id);
}
