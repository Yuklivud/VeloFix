package velofix.dto;

import velofix.model.enums.PaymentStatus;
import velofix.model.enums.PaymentType;
import velofix.model.enums.RepairStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderUpdateForm {
    private UUID orderId;
    private UUID mechanicId;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;
    private RepairStatus status;
    private String repairNote;

    private List<RepairPartDto> repairParts = new ArrayList<>();

    public List<RepairPartDto> getRepairParts() {
        return repairParts;
    }

    public void setRepairParts(List<RepairPartDto> repairParts) {
        this.repairParts = repairParts;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(UUID mechanicId) {
        this.mechanicId = mechanicId;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public RepairStatus getStatus() {
        return status;
    }

    public void setStatus(RepairStatus status) {
        this.status = status;
    }

    public String getRepairNote() {
        return repairNote;
    }

    public void setRepairNote(String repairNote) {
        this.repairNote = repairNote;
    }
}

