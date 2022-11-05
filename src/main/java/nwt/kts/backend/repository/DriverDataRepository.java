package nwt.kts.backend.repository;

import nwt.kts.backend.entity.DriverData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverDataRepository extends JpaRepository<DriverData, Integer> {

    List<DriverData> getAllByIsAnswered(boolean answered);

    DriverData getDriverDataById(Integer id);

}
