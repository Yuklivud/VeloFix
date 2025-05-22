package velofix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import velofix.model.entity.Part;
import velofix.model.entity.PartCategory;
import velofix.service.AuthService;
import velofix.service.PartService;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/parts")
public class PartController {

    private final PartService partService;
    private final AuthService authService;

    public PartController(PartService partService, AuthService authService) {
        this.partService = partService;
        this.authService = authService;
    }

    @GetMapping({"", "/"})
    public String listParts(@RequestParam(required = false) String name,
                            @RequestParam(required = false) UUID categoryId,
                            Model model,
                            @CookieValue(value = "sessiontoken", required = false) String token) {

        authService.getUserFromSession(token).ifPresent(user -> model.addAttribute("sessionUser", user));
        List<Part> parts = partService.findPartsByFilter(name, categoryId);
        List<PartCategory> categories = partService.getAllCategories();

        model.addAttribute("parts", parts);
        model.addAttribute("categories", categories);
        model.addAttribute("filterName", name);
        model.addAttribute("filterCategoryId", categoryId);

        return "adminParts";
    }

    @GetMapping("/add")
    public String showAddForm(Model model, @CookieValue(value = "sessiontoken", required = false) String token) {
        authService.getUserFromSession(token).ifPresent(user -> model.addAttribute("sessionUser", user));
        model.addAttribute("part", new Part());
        model.addAttribute("categories", partService.getAllCategories());
        return "adminEditParts";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable UUID id, Model model,  @CookieValue(value = "sessiontoken", required = false) String token) {
        authService.getUserFromSession(token).ifPresent(user -> model.addAttribute("sessionUser", user));
        Part part = partService.getPartById(id).orElseThrow(() -> new IllegalArgumentException("Invalid part Id:" + id));
        model.addAttribute("part", part);
        model.addAttribute("categories", partService.getAllCategories());
        return "adminEditParts";
    }

    @PostMapping("/save")
    public String savePart(@ModelAttribute Part part, @RequestParam UUID categoryId) {
        PartCategory category = partService.getAllCategories().stream()
                .filter(c -> c.getId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + categoryId));
        part.setCategory(category);
        partService.savePart(part);
        return "redirect:/admin/parts";
    }

    @PostMapping("/{id}/delete")
    public String deletePart(@PathVariable UUID id) {
        partService.deletePart(id);
        return "redirect:/admin/parts";
    }
}
