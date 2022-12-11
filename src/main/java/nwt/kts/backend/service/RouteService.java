package nwt.kts.backend.service;

import nwt.kts.backend.entity.Route;
import nwt.kts.backend.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;

    public Route saveRoute(Route route) {
        return routeRepository.save(route);
    }

    public Route findRouteById(int id) {
        return routeRepository.findRouteById(id);
    }
}
