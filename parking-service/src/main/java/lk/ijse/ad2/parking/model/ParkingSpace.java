package lk.ijse.ad2.parking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "parking_spaces")
public class ParkingSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String spaceCode;
    private String location;
    private String zone;
    private String status; // AVAILABLE, RESERVED, OCCUPIED
    private Double pricePerHour;
    private Long ownerId;

    public ParkingSpace() {}

    public ParkingSpace(Long id, String spaceCode, String location, String zone, String status, Double pricePerHour, Long ownerId) {
        this.id = id;
        this.spaceCode = spaceCode;
        this.location = location;
        this.status = status;
        this.pricePerHour = pricePerHour;
        this.ownerId = ownerId;
        this.zone = zone;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSpaceCode() { return spaceCode; }
    public void setSpaceCode(String spaceCode) { this.spaceCode = spaceCode; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(Double pricePerHour) { this.pricePerHour = pricePerHour; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
