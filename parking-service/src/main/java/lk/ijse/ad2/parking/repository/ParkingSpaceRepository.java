package lk.ijse.ad2.parking.repository;

import lk.ijse.ad2.parking.model.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {
    List<ParkingSpace> findByLocationContainingIgnoreCaseAndStatusContainingIgnoreCaseAndZoneContainingIgnoreCase(
            String location, String status, String zone);
    List<ParkingSpace> findByOwnerId(Long ownerId);
}
