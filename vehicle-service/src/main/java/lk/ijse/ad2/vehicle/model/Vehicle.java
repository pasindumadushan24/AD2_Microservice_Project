package lk.ijse.ad2.vehicle.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String licensePlate;

    private String model;
    private String color;
    private Long userId;
    private String status;


    public Vehicle() {}

    public Vehicle(Long id, String licensePlate, String model, String color, Long userId, String status) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.model = model;
        this.color = color;
        this.userId = userId;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }


    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
