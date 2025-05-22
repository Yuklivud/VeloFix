package velofix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import velofix.model.entity.Order;
import velofix.model.enums.RepairStatus;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query("""
    SELECT DISTINCT o
    FROM Order o
    LEFT JOIN FETCH o.repairParts rp
    LEFT JOIN FETCH rp.part
    WHERE o.customer.id = :customerId
      AND o.status IN :statuses
    """)
    List<Order> findByCustomerIdAndStatusInWithParts(@Param("customerId") UUID customerId,
                                                     @Param("statuses") List<RepairStatus> statuses);


    List<Order> findByStatusIn(List<RepairStatus> statuses);
    List<Order> findByCustomerIdAndStatusIn(UUID customerId, List<RepairStatus> statuses);
}