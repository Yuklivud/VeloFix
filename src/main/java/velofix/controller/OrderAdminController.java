package velofix.controller;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import velofix.dto.OrderFilterDto;
import velofix.dto.OrderUpdateForm;
import velofix.dto.RepairPartDto;
import velofix.model.entity.Order;
import velofix.model.entity.Part;
import velofix.model.entity.RepairPart;
import velofix.model.enums.*;
import velofix.repository.*;
import velofix.service.AuthService;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/orders")
public class OrderAdminController {

    private final UserRepository userRepository;
    private final OrderFilterSupport orderFilterSupport;
    private final BicycleRepository bicycleRepository;
    private final AuthService authService;
    private final PartRepository partRepository;
    private final OrderRepository orderRepository;
    private final RepairPartRepository repairPartRepository;


    @Autowired
    public OrderAdminController(UserRepository userRepository, OrderFilterSupport orderFilterSupport, BicycleRepository bicycleRepository, AuthService authService, PartRepository partRepository, OrderRepository orderRepository, RepairPartRepository repairPartRepository) {
        this.userRepository = userRepository;
        this.orderFilterSupport = orderFilterSupport;
        this.bicycleRepository = bicycleRepository;
        this.authService = authService;
        this.partRepository = partRepository;
        this.orderRepository = orderRepository;
        this.repairPartRepository = repairPartRepository;
    }

    @GetMapping({"", "/"})
    public String getFilteredOrders(Model model,
                                    @ModelAttribute OrderFilterDto filter,
                                    @CookieValue(value = "sessiontoken", required = false) String token) {

        if (token == null || authService.getUserIdFromSession(token).isEmpty()) {
            return "redirect:/signIn";
        }

        UUID userId = authService.getUserIdFromSession(token).get();
        authService.getUserFromSession(token).ifPresent(user -> model.addAttribute("sessionUser", user));

        List<Order> orders = orderFilterSupport.findAllWithFilter(filter);

        model.addAttribute("orders", orders);
        model.addAttribute("filter", filter);
        model.addAttribute("models", BicycleModel.values());
        model.addAttribute("statuses", RepairStatus.values());
        model.addAttribute("paymentStatuses", PaymentStatus.values());
        model.addAttribute("paymentTypes", PaymentType.values());
        model.addAttribute("allParts", partRepository.findAll());
        model.addAttribute("mechanics", userRepository.findAllByRole(UserRole.MECHANIC));

        return "adminOrders";
    }

    @GetMapping("/{id}/edit")
    public String editOrder(@PathVariable UUID id, Model model,
                            @CookieValue(value = "sessiontoken", required = false) String token) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        authService.getUserFromSession(token).ifPresent(user -> model.addAttribute("sessionUser", user));

        OrderUpdateForm form = new OrderUpdateForm();
        form.setOrderId(order.getId());
        form.setMechanicId(order.getMechanic() != null ? order.getMechanic().getId() : null);
        form.setPaymentType(order.getPaymentType());
        form.setPaymentStatus(order.getPaymentStatus());
        form.setStatus(order.getStatus());
        form.setRepairNote(order.getRepairNote());

        List<RepairPartDto> partDtos = order.getRepairParts().stream().map(rp -> {
            RepairPartDto dto = new RepairPartDto();
            dto.setPartId(rp.getPart().getId());
            dto.setQuantityUsed(rp.getQuantityUsed());
            return dto;
        }).collect(Collectors.toList());
        form.setRepairParts(partDtos);

        Map<UUID, String> partNameMap = partRepository.findAll().stream()
                .collect(Collectors.toMap(Part::getId, Part::getName));

        model.addAttribute("partNameMap", partNameMap);
        model.addAttribute("orderForm", form);
        model.addAttribute("mechanics", userRepository.findAllByRole(UserRole.MECHANIC));
        model.addAttribute("paymentTypes", PaymentType.values());
        model.addAttribute("paymentStatuses", PaymentStatus.values());
        model.addAttribute("statuses", RepairStatus.values());
        model.addAttribute("allParts", partRepository.findAll());

        return "adminEditOrder";
    }

    @PostMapping("/{id}/edit")
    @Transactional
    public String updateOrder(@ModelAttribute("orderForm") OrderUpdateForm form,
                              @RequestParam(value = "deletedParts", required = false) String deletedPartsRaw) {
        if (form.getOrderId() == null) {
            throw new IllegalArgumentException("Order ID is required");
        }

        Order order = orderRepository.findById(form.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (form.getMechanicId() != null) {
            userRepository.findById(form.getMechanicId()).ifPresent(order::setMechanic);
        } else {
            order.setMechanic(null);
        }

        Set<UUID> deletedPartIds = new HashSet<>();
        if (deletedPartsRaw != null && !deletedPartsRaw.trim().isEmpty()) {
            for (String partIdStr : deletedPartsRaw.split(",")) {
                try {
                    UUID id = UUID.fromString(partIdStr.trim());
                    deletedPartIds.add(id);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid UUID: " + partIdStr);
                }
            }
        }

        if (!deletedPartIds.isEmpty()) {
            repairPartRepository.deleteByOrderIdAndPartIdIn(form.getOrderId(), deletedPartIds);
        }

        order.setPaymentType(form.getPaymentType());
        order.setPaymentStatus(form.getPaymentStatus());
        order.setStatus(form.getStatus());
        order.setRepairNote(form.getRepairNote());

        order.getRepairParts().clear();
        for (RepairPartDto dto : form.getRepairParts()) {
            Part part = partRepository.findById(dto.getPartId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Part not found"));
            RepairPart repairPart = new RepairPart();
            repairPart.setOrder(order);
            repairPart.setPart(part);
            repairPart.setQuantityUsed(dto.getQuantityUsed());
            order.getRepairParts().add(repairPart);
        }

        BigDecimal total = order.getRepairParts().stream()
                .map(rp -> rp.getPart().getPrice().multiply(BigDecimal.valueOf(rp.getQuantityUsed())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalCost(total);


        orderRepository.save(order);

        return "redirect:/admin/orders";
    }
}
