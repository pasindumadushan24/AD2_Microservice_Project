package lk.ijse.ad2.parking.repository;

import lk.ijse.ad2.parking.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findBySpaceId(Long spaceId);
    List<Reservation> findByVehicleId(Long vehicleId);
}
