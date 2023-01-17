package nwt.kts.backend.repository;

import nwt.kts.backend.entity.DriverData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverDataRepository extends JpaRepository<DriverData, Integer> {

    Page<DriverData> getAllByIsAnswered(boolean answered, Pageable pageable);

    DriverData getDriverDataById(Integer id);

    DriverData getDriverDataByIsAnsweredAndEmail(boolean answered, String email);

}
