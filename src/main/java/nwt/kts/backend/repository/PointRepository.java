package nwt.kts.backend.repository;

import nwt.kts.backend.entity.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Integer> {
    Point findByLatitudeAndLongitude(double latitude, double longitude);

    Page<Point> findAll(Pageable pageable);
}
