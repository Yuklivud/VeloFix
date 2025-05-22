package velofix.model.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class RepairPartId implements Serializable {
    private UUID order;
    private UUID part;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepairPartId that)) return false;
        return Objects.equals(order, that.order) && Objects.equals(part, that.part);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, part);
    }
}
