package velofix.spring.rest;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;
import velofix.controller.OrderAdminController;
import velofix.dto.OrderFilterDto;
import velofix.dto.OrderUpdateForm;
import velofix.dto.RepairPartDto;
import velofix.model.entity.*;
import velofix.model.enums.*;
import velofix.repository.*;
import velofix.service.AuthService;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderAdminControllerTest {

    @Mock UserRepository userRepository;
    @Mock OrderFilterSupport orderFilterSupport;
    @Mock BicycleRepository bicycleRepository;
    @Mock AuthService authService;
    @Mock PartRepository partRepository;
    @Mock OrderRepository orderRepository;
    @Mock RepairPartRepository repairPartRepository;
    @Mock Model model;

    @InjectMocks
    OrderAdminController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getFilteredOrders_noToken_redirectsToSignIn() {
        String view = controller.getFilteredOrders(model, new OrderFilterDto(), null);
        assertEquals("redirect:/signIn", view);
        verifyNoInteractions(orderFilterSupport, partRepository, userRepository);
    }

    @Test
    void getFilteredOrders_tokenPresent_userNotFound_redirectsToSignIn() {
        String token = "token";
        when(authService.getUserIdFromSession(token)).thenReturn(Optional.empty());

        String view = controller.getFilteredOrders(model, new OrderFilterDto(), token);
        assertEquals("redirect:/signIn", view);
    }

    @Test
    void getFilteredOrders_validToken_addsAttributesAndReturnsView() {
        String token = "token";
        UUID userId = UUID.randomUUID();
        User user = new User();
        List<Order> orders = List.of(new Order());

        when(authService.getUserIdFromSession(token)).thenReturn(Optional.of(userId));
        when(authService.getUserFromSession(token)).thenReturn(Optional.of(user));
        when(orderFilterSupport.findAllWithFilter(any())).thenReturn(orders);
        when(partRepository.findAll()).thenReturn(List.of(new Part()));
        when(userRepository.findAllByRole(UserRole.MECHANIC)).thenReturn(List.of(new User()));

        OrderFilterDto filter = new OrderFilterDto();

        String view = controller.getFilteredOrders(model, filter, token);

        assertEquals("adminOrders", view);

        verify(model).addAttribute("sessionUser", user);
        verify(model).addAttribute("orders", orders);
        verify(model).addAttribute("filter", filter);
        verify(model).addAttribute(eq("models"), any());
        verify(model).addAttribute(eq("statuses"), any());
        verify(model).addAttribute(eq("paymentStatuses"), any());
        verify(model).addAttribute(eq("paymentTypes"), any());
        verify(model).addAttribute(eq("allParts"), any());
        verify(model).addAttribute(eq("mechanics"), any());
    }

    @Test
    void editOrder_orderNotFound_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.editOrder(id, model, "token"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void editOrder_orderFound_populatesModelAndReturnsView() {
        UUID id = UUID.randomUUID();
        Order order = new Order();
        order.setId(id);
        order.setPaymentType(PaymentType.CASH);
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(RepairStatus.IN_PROGRESS);
        order.setRepairNote("note");
        User mechanic = new User();
        mechanic.setId(UUID.randomUUID());
        order.setMechanic(mechanic);

        RepairPart rp = new RepairPart();
        Part part = new Part();
        part.setId(UUID.randomUUID());
        part.setName("Wheel");
        rp.setPart(part);
        rp.setQuantityUsed(2);
        order.setRepairParts(List.of(rp));

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(authService.getUserFromSession("token")).thenReturn(Optional.of(new User()));
        when(partRepository.findAll()).thenReturn(List.of(part));
        when(userRepository.findAllByRole(UserRole.MECHANIC)).thenReturn(List.of(mechanic));

        String view = controller.editOrder(id, model, "token");

        assertEquals("adminEditOrder", view);
        verify(model).addAttribute(eq("orderForm"), any(OrderUpdateForm.class));
        verify(model).addAttribute(eq("mechanics"), any());
        verify(model).addAttribute(eq("paymentTypes"), any());
        verify(model).addAttribute(eq("paymentStatuses"), any());
        verify(model).addAttribute(eq("statuses"), any());
        verify(model).addAttribute(eq("allParts"), any());
    }

    @Test
    void updateOrder_noOrderId_throwsException() {
        OrderUpdateForm form = new OrderUpdateForm(); // orderId == null
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.updateOrder(form, null));
        assertEquals("Order ID is required", ex.getMessage());
    }

    @Test
    void updateOrder_orderNotFound_throwsNotFound() {
        UUID orderId = UUID.randomUUID();
        OrderUpdateForm form = new OrderUpdateForm();
        form.setOrderId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.updateOrder(form, null));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void updateOrder_successfulUpdate_returnsRedirect() {
        UUID orderId = UUID.randomUUID();
        UUID mechanicId = UUID.randomUUID();
        UUID partId1 = UUID.randomUUID();
        UUID partId2 = UUID.randomUUID();

        User mechanic = new User();
        mechanic.setId(mechanicId);

        Part part1 = new Part();
        part1.setId(partId1);
        part1.setPrice(new BigDecimal("10"));

        Part part2 = new Part();
        part2.setId(partId2);
        part2.setPrice(new BigDecimal("20"));

        Order order = new Order();
        order.setId(orderId);
        order.setRepairParts(new ArrayList<>());

        OrderUpdateForm form = new OrderUpdateForm();
        form.setOrderId(orderId);
        form.setMechanicId(mechanicId);
        form.setPaymentType(PaymentType.CREDIT_CARD);
        form.setPaymentStatus(PaymentStatus.UNPAID);
        form.setStatus(RepairStatus.COMPLETED);
        form.setRepairNote("note");

        RepairPartDto dto1 = new RepairPartDto();
        dto1.setPartId(partId1);
        dto1.setQuantityUsed(2);

        RepairPartDto dto2 = new RepairPartDto();
        dto2.setPartId(partId2);
        dto2.setQuantityUsed(1);

        form.setRepairParts(List.of(dto1, dto2));

        String deletedPartsRaw = partId1.toString();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userRepository.findById(mechanicId)).thenReturn(Optional.of(mechanic));
        when(partRepository.findById(partId1)).thenReturn(Optional.of(part1));
        when(partRepository.findById(partId2)).thenReturn(Optional.of(part2));

        String view = controller.updateOrder(form, deletedPartsRaw);

        verify(repairPartRepository).deleteByOrderIdAndPartIdIn(orderId, Set.of(partId1));
        verify(orderRepository).save(order);

        assertEquals(mechanic, order.getMechanic());
        assertEquals(PaymentType.CREDIT_CARD, order.getPaymentType());
        assertEquals(PaymentStatus.UNPAID, order.getPaymentStatus());
        assertEquals(RepairStatus.COMPLETED, order.getStatus());
        assertEquals("note", order.getRepairNote());

        assertEquals(new BigDecimal("40"), order.getTotalCost());

        assertEquals(2, order.getRepairParts().size());

        assertEquals("redirect:/admin/orders", view);
    }

    @Test
    void updateOrder_partNotFound_throwsNotFound() {
        UUID orderId = UUID.randomUUID();
        UUID partId = UUID.randomUUID();

        Order order = new Order();
        order.setId(orderId);
        order.setRepairParts(new ArrayList<>());

        OrderUpdateForm form = new OrderUpdateForm();
        form.setOrderId(orderId);
        form.setRepairParts(List.of(new RepairPartDto() {{
            setPartId(partId);
            setQuantityUsed(1);
        }}));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(partRepository.findById(partId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.updateOrder(form, null));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Part not found", ex.getReason());
    }
}
