package nwt.kts.backend.repository;

import nwt.kts.backend.entity.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageDataRepository extends JpaRepository<ImageData, Integer> {
}
