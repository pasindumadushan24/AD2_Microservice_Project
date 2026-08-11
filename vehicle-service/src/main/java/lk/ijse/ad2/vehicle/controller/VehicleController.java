package lk.ijse.ad2.vehicle.controller;

import lk.ijse.ad2.vehicle.model.Vehicle;
import lk.ijse.ad2.vehicle.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping
    public ResponseEntity<?> registerVehicle(@RequestBody Vehicle vehicle) {
        if (vehicleRepository.findByLicensePlate(vehicle.getLicensePlate()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vehicle with this license plate is already registered.");
        }
        vehicle.setStatus("OUTSIDE");
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicle);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVehicleById(@PathVariable Long id) {
        return vehicleRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle not found."));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Vehicle>> getVehiclesByUserId(@PathVariable Long userId) {
        List<Vehicle> vehicles = vehicleRepository.findByUserId(userId);
        return ResponseEntity.ok(vehicles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicleDetails) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(id);
        if (vehicleOpt.isPresent()) {
            Vehicle vehicle = vehicleOpt.get();
            vehicle.setModel(vehicleDetails.getModel());
            vehicle.setColor(vehicleDetails.getColor());
            vehicle.setLicensePlate(vehicleDetails.getLicensePlate());
            Vehicle updatedVehicle = vehicleRepository.save(vehicle);
            return ResponseEntity.ok(updatedVehicle);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle not found.");
    }

    @PostMapping("/{licensePlate}/entry")
    public ResponseEntity<?> simulateEntry(@PathVariable String licensePlate) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findByLicensePlate(licensePlate);
        if (vehicleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle not found.");
        }
        Vehicle vehicle = vehicleOpt.get();
        if ("IN_PARKING".equals(vehicle.getStatus())) {
            return ResponseEntity.badRequest().body("Vehicle is already in the parking area.");
        }
        vehicle.setStatus("IN_PARKING");
        vehicleRepository.save(vehicle);

        // Optional: Notify parking-service that vehicle entered (can trigger slot occupation)
        try {
            // We can send a message or perform actions here if needed
            // For now, return success
        } catch (Exception ignored) {}

        return ResponseEntity.ok("Vehicle with license plate " + licensePlate + " entered. Status updated to IN_PARKING.");
    }

    @PostMapping("/{licensePlate}/exit")
    public ResponseEntity<?> simulateExit(@PathVariable String licensePlate) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findByLicensePlate(licensePlate);
        if (vehicleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle not found.");
        }
        Vehicle vehicle = vehicleOpt.get();
        if ("OUTSIDE".equals(vehicle.getStatus())) {
            return ResponseEntity.badRequest().body("Vehicle is already outside the parking area.");
        }
        vehicle.setStatus("OUTSIDE");
        vehicleRepository.save(vehicle);

        return ResponseEntity.ok("Vehicle with license plate " + licensePlate + " exited. Status updated to OUTSIDE.");
    }
}
