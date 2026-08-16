package lk.ijse.ad2.parking.controller;

import lk.ijse.ad2.parking.model.ParkingSpace;
import lk.ijse.ad2.parking.model.Reservation;
import lk.ijse.ad2.parking.repository.ParkingSpaceRepository;
import lk.ijse.ad2.parking.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/parking")
public class ParkingController {

    @Autowired
    private ParkingSpaceRepository parkingSpaceRepository;


    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/spaces")
    public ResponseEntity<List<ParkingSpace>> getSpaces(
            @RequestParam(required = false, defaultValue = "") String location,
            @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(required = false, defaultValue = "") String zone) {
        
        List<ParkingSpace> spaces = parkingSpaceRepository
                .findByLocationContainingIgnoreCaseAndStatusContainingIgnoreCaseAndZoneContainingIgnoreCase(
                        location, status, zone);
        return ResponseEntity.ok(spaces);
    }

    @PostMapping("/spaces")
    public ResponseEntity<ParkingSpace> createSpace(@RequestBody ParkingSpace space) {
        if (space.getStatus() == null) {
            space.setStatus("AVAILABLE");
        }
        ParkingSpace saved = parkingSpaceRepository.save(space);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/spaces/{id}/status")
    public ResponseEntity<?> updateSpaceStatus(@PathVariable Long id, @RequestParam String status) {
        Optional<ParkingSpace> spaceOpt = parkingSpaceRepository.findById(id);
        if (spaceOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking space not found.");
        }
        ParkingSpace space = spaceOpt.get();
        space.setStatus(status.toUpperCase());
        parkingSpaceRepository.save(space);
        return ResponseEntity.ok("Parking space status updated to " + status.toUpperCase() + " successfully.");
    }

    @PostMapping("/spaces/reserve")
    public ResponseEntity<?> reserveSpace(@RequestBody Map<String, Object> request) {
        try {
            Long spaceId = Long.valueOf(request.get("spaceId").toString());
            Long userId = Long.valueOf(request.get("userId").toString());
            Long vehicleId = Long.valueOf(request.get("vehicleId").toString());
            LocalDateTime startTime = LocalDateTime.parse(request.get("startTime").toString());
            LocalDateTime endTime = LocalDateTime.parse(request.get("endTime").toString());
            String cardNumber = request.get("cardNumber").toString();

            Optional<ParkingSpace> spaceOpt = parkingSpaceRepository.findById(spaceId);
            if (spaceOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking space not found.");
            }
            ParkingSpace space = spaceOpt.get();
            if (!"AVAILABLE".equals(space.getStatus())) {
                return ResponseEntity.badRequest().body("Parking space is not available.");
            }

            long hours = Duration.between(startTime, endTime).toHours();
            if (hours <= 0) {
                hours = 1; 
            }
            double totalAmount = hours * space.getPricePerHour();

            Reservation reservation = new Reservation();
            reservation.setSpaceId(spaceId);
            reservation.setUserId(userId);
            reservation.setVehicleId(vehicleId);
            reservation.setStartTime(startTime);
            reservation.setEndTime(endTime);
            reservation.setStatus("PENDING_PAYMENT");
            reservation.setTotalAmount(totalAmount);
            Reservation savedReservation = reservationRepository.save(reservation);

            Map<String, Object> paymentRequest = new HashMap<>();
            paymentRequest.put("userId", userId);
            paymentRequest.put("reservationId", savedReservation.getId());
            paymentRequest.put("amount", totalAmount);
            paymentRequest.put("cardNumber", cardNumber);

            String paymentUrl = "http://payment-service/payments/charge";
            Map<?, ?> paymentResponse = restTemplate.postForObject(paymentUrl, paymentRequest, Map.class);

            if (paymentResponse != null && "SUCCESS".equals(paymentResponse.get("status"))) {
                savedReservation.setStatus("ACTIVE");
                reservationRepository.save(savedReservation);

                space.setStatus("RESERVED");
                parkingSpaceRepository.save(space);

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Reservation confirmed and payment processed successfully.");
                response.put("reservation", savedReservation);
                response.put("receiptNumber", paymentResponse.get("receiptNumber"));
                response.put("transactionId", paymentResponse.get("id"));

                return ResponseEntity.ok(response);
            } else {
                savedReservation.setStatus("FAILED");
                reservationRepository.save(savedReservation);
                return ResponseEntity.badRequest().body("Payment failed. Reservation cancelled.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing reservation: " + e.getMessage());
        }
    }

    @PostMapping("/spaces/{id}/release")
    public ResponseEntity<?> releaseSpace(@PathVariable Long id) {
        Optional<ParkingSpace> spaceOpt = parkingSpaceRepository.findById(id);
        if (spaceOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking space not found.");
        }
        ParkingSpace space = spaceOpt.get();
        space.setStatus("AVAILABLE");
        parkingSpaceRepository.save(space);

        List<Reservation> reservations = reservationRepository.findBySpaceId(id);
        for (Reservation res : reservations) {
            if ("ACTIVE".equals(res.getStatus())) {
                res.setStatus("COMPLETED");
                res.setEndTime(LocalDateTime.now());
                reservationRepository.save(res);
            }
        }

        return ResponseEntity.ok("Parking space is now released and set to AVAILABLE.");
    }

    @GetMapping("/reservations/user/{userId}")
    public ResponseEntity<List<Reservation>> getReservationsByUserId(@PathVariable Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        return ResponseEntity.ok(reservations);
    }
}
