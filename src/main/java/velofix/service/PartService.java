package velofix.service;

import org.springframework.stereotype.Service;
import velofix.model.entity.Part;
import velofix.model.entity.PartCategory;
import velofix.repository.PartCategoryRepository;
import velofix.repository.PartRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PartService {

    private final PartRepository partRepository;
    private final PartCategoryRepository partCategoryRepository;

    public PartService(PartRepository partRepository, PartCategoryRepository partCategoryRepository) {
        this.partRepository = partRepository;
        this.partCategoryRepository = partCategoryRepository;
    }

    public List<Part> findPartsByFilter(String name, UUID categoryId) {
        PartCategory category = null;
        if (categoryId != null) {
            category = partCategoryRepository.findById(categoryId).orElse(null);
        }
        return partRepository.findByNameAndCategory(name, category);
    }

    public List<PartCategory> getAllCategories() {
        return partCategoryRepository.findAll();
    }

    public Optional<Part> getPartById(UUID id) {
        return partRepository.findById(id);
    }

    public Part savePart(Part part) {
        if (part.getId() == null) {
            part.setId(UUID.randomUUID());
        }
        return partRepository.save(part);
    }

    public void deletePart(UUID id) {
        partRepository.deleteById(id);
    }
}
