package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.RouteCreationDTO;
import nwt.kts.backend.dto.returnDTO.RouteDTO;
import nwt.kts.backend.entity.Point;
import nwt.kts.backend.entity.Route;
import nwt.kts.backend.service.PointService;
import nwt.kts.backend.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Iterator;

@RestController
@RequestMapping("/routes")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @Autowired
    private PointService pointService;

    @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RouteDTO> addRoute(@RequestBody RouteCreationDTO routeCreationDTO) {
        Route newRoute = new Route(routeCreationDTO);
        if (pointService.findPointByLatitudeAndLongitude(newRoute.getStartPoint()) == null) {
            pointService.savePoint(newRoute.getStartPoint());
        }
        if (pointService.findPointByLatitudeAndLongitude(newRoute.getEndPoint()) == null) {
            pointService.savePoint(newRoute.getEndPoint());
        }
        Iterator<Point> i = newRoute.getRoutePath().iterator();
        while (i.hasNext()) {
            Point point = i.next();
            if (pointService.findPointByLatitudeAndLongitude(point) == null) {
                pointService.savePoint(point);
            } else {
                i.remove();
            }
        }
        newRoute = routeService.saveRoute(newRoute);
        return new ResponseEntity<>(new RouteDTO(newRoute), HttpStatus.CREATED);
    }

    @GetMapping(value="/{routeId}", produces = "application/json")
    public ResponseEntity<RouteDTO> getRouteById(@PathVariable Integer routeId) {
        Route route = routeService.findRouteById(routeId);
        if (route == null) {
            throw new EntityNotFoundException("Route with the specified ID does not exist!");
        }
        return new ResponseEntity<>(new RouteDTO(route), HttpStatus.OK);
    }
}
