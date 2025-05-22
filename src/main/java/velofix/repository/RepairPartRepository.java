package velofix.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import velofix.model.entity.Order;
import velofix.model.entity.Part;
import velofix.model.entity.RepairPart;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RepairPartRepository extends JpaRepository<RepairPart, UUID> {
    Optional<RepairPart> findByOrderAndPart(Order order, Part part);

    @Transactional
    @Modifying
    @Query("DELETE FROM RepairPart rp WHERE rp.order.id = :orderId AND rp.part.id IN :partIds")
    void deleteByOrderIdAndPartIdIn(@Param("orderId") UUID orderId, @Param("partIds") Set<UUID> partIds);
}
