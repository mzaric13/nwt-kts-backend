package nwt.kts.backend.service;

import nwt.kts.backend.entity.Point;
import nwt.kts.backend.repository.PointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointService {

    @Autowired
    private PointRepository pointRepository;

    public Point savePoint(Point point) {
        return pointRepository.save(point);
    }
}
