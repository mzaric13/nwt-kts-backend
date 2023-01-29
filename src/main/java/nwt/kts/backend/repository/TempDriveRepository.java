package nwt.kts.backend.repository;

import nwt.kts.backend.entity.Status;
import nwt.kts.backend.entity.TempDrive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TempDriveRepository extends JpaRepository<TempDrive, Integer> {

    TempDrive findTempDriveById(int id);

    List<TempDrive> findAllByStatus(Status reserved);
}
