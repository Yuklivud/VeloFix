package velofix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ExistingBicycleOrderRequestDto {

    @NotNull(message = "Bicycle ID is required")
    private UUID bicycleId;

    @NotBlank(message = "Repair shop address is required")
    private String repairShopAddress;

    private boolean warrantyRepair;

    @NotBlank(message = "Issue description is required")
    private String issueDescription;

    public @NotNull(message = "Bicycle ID is required") UUID getBicycleId() {
        return bicycleId;
    }

    public void setBicycleId(@NotNull(message = "Bicycle ID is required") UUID bicycleId) {
        this.bicycleId = bicycleId;
    }

    public @NotBlank(message = "Repair shop address is required") String getRepairShopAddress() {
        return repairShopAddress;
    }

    public void setRepairShopAddress(@NotBlank(message = "Repair shop address is required") String repairShopAddress) {
        this.repairShopAddress = repairShopAddress;
    }

    public boolean isWarrantyRepair() {
        return warrantyRepair;
    }

    public void setWarrantyRepair(boolean warrantyRepair) {
        this.warrantyRepair = warrantyRepair;
    }

    public @NotBlank(message = "Issue description is required") String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(@NotBlank(message = "Issue description is required") String issueDescription) {
        this.issueDescription = issueDescription;
    }
}
