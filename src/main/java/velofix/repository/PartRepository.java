package velofix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import velofix.model.entity.Part;
import velofix.model.entity.PartCategory;

import java.util.List;
import java.util.UUID;

@Repository
public interface PartRepository extends JpaRepository<Part, UUID> {

    @Query("SELECT p FROM Part p " +
            "WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))) " +
            "AND (:category IS NULL OR p.category = :category)")
    List<Part> findByNameAndCategory(@Param("name") String name, @Param("category") PartCategory category);


}
