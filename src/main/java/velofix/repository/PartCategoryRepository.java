package velofix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import velofix.model.entity.PartCategory;

import java.util.UUID;

@Repository
public interface PartCategoryRepository extends JpaRepository<PartCategory, UUID> {
}
