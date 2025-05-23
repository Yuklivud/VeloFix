package velofix.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class RepairPartDto {
    private UUID partId;
    private Integer quantityUsed;
    private String partName;
    private BigDecimal partPrice;
    private BigDecimal sum;

    public void calculateSum() {
        if (partPrice != null && quantityUsed != null) {
            this.sum = partPrice.multiply(BigDecimal.valueOf(quantityUsed));
        } else {
            this.sum = BigDecimal.ZERO;
        }
    }

    public BigDecimal getSum() {
        return sum;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public BigDecimal getPartPrice() {
        return partPrice;
    }

    public void setPartPrice(BigDecimal partPrice) {
        this.partPrice = partPrice;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public UUID getPartId() {
        return partId;
    }

    public void setPartId(UUID partId) {
        this.partId = partId;
    }

    public Integer getQuantityUsed() {
        return quantityUsed;
    }

    public void setQuantityUsed(Integer quantityUsed) {
        this.quantityUsed = quantityUsed;
    }
}
