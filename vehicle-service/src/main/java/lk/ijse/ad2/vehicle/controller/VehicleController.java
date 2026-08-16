package lk.ijse.ad2.vehicle.controller;

import lk.ijse.ad2.vehicle.dto.VehicleDTO;
import lk.ijse.ad2.vehicle.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {


    @Autowired
    private VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<?> registerVehicle(@RequestBody VehicleDTO vehicleDTO) {
        try {
            VehicleDTO registered = vehicleService.registerVehicle(vehicleDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(registered);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVehicleById(@PathVariable Long id) {
        VehicleDTO vehicleDTO = vehicleService.getVehicleById(id);
        if (vehicleDTO != null) {
            return ResponseEntity.ok(vehicleDTO);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle not found.");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VehicleDTO>> getVehiclesByUserId(@PathVariable Long userId) {
        List<VehicleDTO> vehicles = vehicleService.getVehiclesByUserId(userId);
        return ResponseEntity.ok(vehicles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVehicle(@PathVariable Long id, @RequestBody VehicleDTO vehicleDTO) {
        VehicleDTO updated = vehicleService.updateVehicle(id, vehicleDTO);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle not found.");
    }

    @PostMapping("/{licensePlate}/entry")
    public ResponseEntity<?> simulateEntry(@PathVariable String licensePlate) {
        try {
            String result = vehicleService.simulateEntry(licensePlate);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{licensePlate}/exit")
    public ResponseEntity<?> simulateExit(@PathVariable String licensePlate) {
        try {
            String result = vehicleService.simulateExit(licensePlate);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
