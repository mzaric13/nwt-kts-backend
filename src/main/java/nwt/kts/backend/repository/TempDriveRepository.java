package nwt.kts.backend.repository;

import nwt.kts.backend.entity.TempDrive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempDriveRepository extends JpaRepository<TempDrive, Integer> {

    TempDrive findTempDriveById(int id);
}
