package nwt.kts.backend.repository;

import nwt.kts.backend.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Integer> {
    Point findByLatitudeAndLongitude(double latitude, double longitude);
}
