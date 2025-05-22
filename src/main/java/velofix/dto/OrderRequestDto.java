package velofix.dto;

import jakarta.validation.constraints.*;
import velofix.model.enums.BicycleModel;

public class OrderRequestDto {

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotNull(message = "Model is required")
    private BicycleModel model;

    @Min(1900)
    @Max(2100)
    private Integer year;

    @NotBlank(message = "Serial number is required")
    private String serialNumber;

    @NotBlank(message = "Repair shop address is required")
    private String repairShopAddress;

    private boolean warrantyRepair;

    @NotBlank(message = "Issue description is required")
    private String issueDescription;

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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getRepairShopAddress() {
        return repairShopAddress;
    }

    public void setRepairShopAddress(String repairShopAddress) {
        this.repairShopAddress = repairShopAddress;
    }

    public boolean isWarrantyRepair() {
        return warrantyRepair;
    }

    public void setWarrantyRepair(boolean warrantyRepair) {
        this.warrantyRepair = warrantyRepair;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }
}

