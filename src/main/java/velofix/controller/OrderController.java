package velofix.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import velofix.dto.ExistingBicycleOrderRequestDto;
import velofix.dto.OrderRequestDto;
import velofix.model.entity.Bicycle;
import velofix.model.entity.Notification;
import velofix.model.entity.Order;
import velofix.model.entity.User;
import velofix.model.enums.BicycleModel;
import velofix.model.enums.PaymentStatus;
import velofix.model.enums.RepairStatus;
import velofix.model.enums.UserRole;
import velofix.repository.BicycleRepository;
import velofix.repository.NotificationRepository;
import velofix.repository.OrderRepository;
import velofix.repository.UserRepository;
import velofix.service.AuthService;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class OrderController {


    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final BicycleRepository bicycleRepository;
    private final AuthService authService;

    @Autowired
    public OrderController(NotificationRepository notificationRepository, UserRepository userRepository, OrderRepository orderRepository, BicycleRepository bicycleRepository, AuthService authService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.bicycleRepository = bicycleRepository;
        this.authService = authService;
    }

    @GetMapping("/order")
    public String listOrders(
            @CookieValue(value = "sessiontoken", required = false) String token,
            Model model) {

        if (token == null || authService.getUserIdFromSession(token).isEmpty()) {
            return "redirect:/signIn";
        }

        UUID userId = authService.getUserIdFromSession(token).get();
        authService.getUserFromSession(token).ifPresent(user -> model.addAttribute("sessionUser", user));
        List<RepairStatus> statuses = List.of(
                RepairStatus.NEW,
                RepairStatus.IN_PROGRESS,
                RepairStatus.WAINTING
        );

        List<Order> orders = orderRepository.findByCustomerIdAndStatusIn(userId, statuses);
        model.addAttribute("orders", orders);
        return "CurrentOrders";
    }


    @GetMapping("/order/history")
    public String historyOrders(Model model, @CookieValue(value = "sessiontoken", required = false) String token) {
        if (token == null || authService.getUserIdFromSession(token).isEmpty()) {
            return "redirect:/signIn";
        }

        UUID customerId = authService.getUserIdFromSession(token).get();

        authService.getUserFromSession(token).ifPresent(user -> model.addAttribute("sessionUser", user));

        List<RepairStatus> statuses = List.of(
                RepairStatus.COMPLETED,
                RepairStatus.REJECTED
        );

        List<Order> orders = orderRepository.findByCustomerIdAndStatusInWithParts(customerId, statuses);
        model.addAttribute("orders", orders);
        return "history";
    }

    @ModelAttribute("bicycleModels")
    public BicycleModel[] bicycleModels() {
        return BicycleModel.values();
    }


    @GetMapping("/create")
    public String createOrderForm(Model model, @CookieValue(value = "sessiontoken", required = false) String token, HttpServletRequest request) {
        model.addAttribute("orderRequestDto", new OrderRequestDto());
        model.addAttribute("repairShops", List.of("Katowice, St. Maria 3b", "Warszawa, Loisa Ave. 45", "Gdansk, Morska Blvd. 7"));
        model.addAttribute("bicycleModels", BicycleModel.values());

        authService.getUserFromSession(token).ifPresent(user -> model.addAttribute("sessionUser", user));

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("_csrf", csrfToken);

        return "CreateNewBidWithNewBicycle";
    }

    @GetMapping("/create/from-existing")
    public String showCreateOrderFromExistingForm(Model model,
                                                  @CookieValue(value = "sessiontoken", required = false) String token) {
        if (token == null || authService.getUserIdFromSession(token).isEmpty()) {
            return "redirect:/signIn";
        }

        UUID userId = authService.getUserIdFromSession(token).get();
        List<Bicycle> bicycles = bicycleRepository.findByCustomerId(userId);


        authService.getUserFromSession(token).ifPresent(user -> model.addAttribute("sessionUser", user));
        model.addAttribute("bicycles", bicycles);
        model.addAttribute("repairShops", List.of("Katowice, St. Maria 3b", "Warszawa, Loisa Ave. 45", "Gdansk, Morska Blvd. 7"));
        model.addAttribute("orderRequestDto", new ExistingBicycleOrderRequestDto());

        return "CreateBidFromExistingBicycle";
    }

    @PostMapping("/create/from-existing")
    public String createOrderFromExisting(@ModelAttribute("orderRequestDto") @Valid ExistingBicycleOrderRequestDto dto,
                                          BindingResult bindingResult,
                                          @CookieValue(value = "sessiontoken", required = false) String token,
                                          Model model) {
        if (token == null || authService.getUserIdFromSession(token).isEmpty()) {
            return "redirect:/signIn";
        }

        UUID userId = authService.getUserIdFromSession(token).get();
        List<Bicycle> bicycles = bicycleRepository.findByCustomerId(userId);
        model.addAttribute("bicycles", bicycles);
        model.addAttribute("repairShops", List.of("Katowice, St. Maria 3b", "Warszawa, Loisa Ave. 45", "Gdansk, Morska Blvd. 7"));

        if (bindingResult.hasErrors()) {
            return "CreateBidFromExistingBicycle";
        }

        Bicycle bicycle = bicycleRepository.findById(dto.getBicycleId())
                .orElseThrow(() -> new IllegalArgumentException("Bicycle not found"));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setBicycle(bicycle);
        order.setCustomer(bicycle.getCustomer());
        order.setWarrantyRepair(dto.isWarrantyRepair());
        order.setIssueDescription(dto.getIssueDescription());
        order.setStatus(RepairStatus.NEW);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setCreatedAt(LocalDateTime.now());

        orderRepository.save(order);

        return "redirect:/order";
    }



    @PostMapping("/create")
    public String createOrder(@ModelAttribute("orderRequestDto") @Valid OrderRequestDto dto,
                              BindingResult bindingResult,
                              @CookieValue(value = "sessiontoken", required = false) String token,
                              Model model) {

        if (token == null || authService.getUserIdFromSession(token).isEmpty()) {
            return "redirect:/signIn";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("bicycleModels", BicycleModel.values());
            model.addAttribute("repairShops", userRepository.findByRole(UserRole.MANAGER));
            return "CreateNewBidWithNewBicycle";
        }

        UUID customerId = authService.getUserIdFromSession(token).get();
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Bicycle bicycle = new Bicycle();
        bicycle.setId(UUID.randomUUID());
        bicycle.setCustomer(customer);
        bicycle.setBrand(dto.getBrand());
        bicycle.setModel(dto.getModel());
        bicycle.setYear(dto.getYear());
        bicycle.setSerialNumber(dto.getSerialNumber());
        bicycle.setWarrantyStatus(dto.isWarrantyRepair());
        bicycleRepository.save(bicycle);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setBicycle(bicycle);
        order.setCustomer(customer);
        order.setStatus(RepairStatus.NEW);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setWarrantyRepair(dto.isWarrantyRepair());
        order.setIssueDescription(dto.getIssueDescription());
        order.setCreatedAt(LocalDateTime.now());
        orderRepository.save(order);

        return "redirect:/order";
    }

    @PostMapping("/orders/{id}/cancel")
    @ResponseBody
    public ResponseEntity<?> cancelOrder(@PathVariable UUID id,
                                         @CookieValue(value = "sessiontoken", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<UUID> userIdOpt = authService.getUserIdFromSession(token);
        if (userIdOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = userIdOpt.get();
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOpt.get();
        if (!order.getCustomer().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        orderRepository.delete(order);
        return ResponseEntity.ok().build();
    }
}