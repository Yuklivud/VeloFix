package velofix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import velofix.model.entity.Bicycle;

import java.util.List;
import java.util.UUID;

@Repository
public interface BicycleRepository extends JpaRepository<Bicycle, UUID> {
    List<Bicycle> findByCustomerId(UUID customerId);
}
