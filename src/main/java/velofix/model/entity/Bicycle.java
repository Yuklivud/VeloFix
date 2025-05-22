package velofix.model.entity;

import jakarta.persistence.*;
import velofix.model.enums.BicycleModel;

import java.util.UUID;

@Entity
@Table(name = "bicycle")
public class Bicycle {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(length = 100)
    private String brand;

    @Enumerated(EnumType.STRING)
    private BicycleModel model;

    private Integer year;

    @Column(name = "warranty_status")
    private Boolean warrantyStatus;

    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BicycleModel getModel() {
        return model;
    }

    public void setModel(BicycleModel model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Boolean getWarrantyStatus() {
        return warrantyStatus;
    }

    public void setWarrantyStatus(Boolean warrantyStatus) {
        this.warrantyStatus = warrantyStatus;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
