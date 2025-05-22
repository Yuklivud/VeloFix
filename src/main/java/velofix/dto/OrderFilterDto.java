package velofix.dto;

import velofix.model.enums.BicycleModel;
import velofix.model.enums.PaymentStatus;
import velofix.model.enums.PaymentType;
import velofix.model.enums.RepairStatus;

import java.time.LocalDate;
import java.util.UUID;

public class OrderFilterDto {

    public BicycleModel bicycleModel;
    public RepairStatus status;
    public PaymentStatus paymentStatus;
    public PaymentType paymentType;
    public LocalDate createdAt;

    public String customerFullName;
    public String mechanicFullName;
    public String bicycleBrand;
    public UUID orderId;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public BicycleModel getBicycleModel() {
        return bicycleModel;
    }

    public void setBicycleModel(BicycleModel bicycleModel) {
        this.bicycleModel = bicycleModel;
    }

    public RepairStatus getStatus() {
        return status;
    }

    public void setStatus(RepairStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getCustomerFullName() {
        return customerFullName;
    }

    public void setCustomerFullName(String customerFullName) {
        this.customerFullName = customerFullName;
    }

    public String getMechanicFullName() {
        return mechanicFullName;
    }

    public void setMechanicFullName(String mechanicFullName) {
        this.mechanicFullName = mechanicFullName;
    }

    public String getBicycleBrand() {
        return bicycleBrand;
    }

    public void setBicycleBrand(String bicycleBrand) {
        this.bicycleBrand = bicycleBrand;
    }
}
