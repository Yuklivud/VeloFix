package velofix.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import velofix.dto.OrderUpdateForm;
import velofix.model.entity.Order;
import velofix.model.entity.Part;
import velofix.model.entity.RepairPart;
import velofix.repository.OrderRepository;
import velofix.repository.PartRepository;
import velofix.repository.RepairPartRepository;
import velofix.repository.UserRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PartRepository partRepository;
    private final RepairPartRepository repairPartRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        PartRepository partRepository,
                        RepairPartRepository repairPartRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.partRepository = partRepository;
        this.repairPartRepository = repairPartRepository;
    }

    public void updateOrder(OrderUpdateForm form, Set<UUID> deletedPartIds) {
        Optional<Order> optOrder = orderRepository.findById(form.getOrderId());
        if (optOrder.isEmpty()) return;
        Order order = optOrder.get();

        if (form.getMechanicId() != null) {
            userRepository.findById(form.getMechanicId())
                    .ifPresent(order::setMechanic);
        } else {
            order.setMechanic(null);
        }

        order.setStatus(form.getStatus());
        order.setPaymentStatus(form.getPaymentStatus());
        order.setPaymentType(form.getPaymentType());
        order.setRepairNote(form.getRepairNote());

        orderRepository.save(order);
    }

    // Добавляем запчасть к заказу с количеством
    public void addPartToOrder(UUID orderId, UUID partId, int quantityUsed) {
        if (quantityUsed < 1) return;

        Optional<Order> optOrder = orderRepository.findById(orderId);
        Optional<Part> optPart = partRepository.findById(partId);
        if (optOrder.isEmpty() || optPart.isEmpty()) return;

        Order order = optOrder.get();
        Part part = optPart.get();

        // Проверяем, есть ли уже такая запчасть в заказе
        Optional<RepairPart> existingRepairPart = repairPartRepository.findByOrderAndPart(order, part);

        if (existingRepairPart.isPresent()) {
            RepairPart rp = existingRepairPart.get();
            rp.setQuantityUsed(rp.getQuantityUsed() + quantityUsed); // увеличиваем кол-во
            repairPartRepository.save(rp);
        } else {
            RepairPart rp = new RepairPart();
            rp.setOrder(order);
            rp.setPart(part);
            rp.setQuantityUsed(quantityUsed);
            repairPartRepository.save(rp);
        }
    }

    // Удаляем запчасть из заказа
    public void removePartFromOrder(UUID orderId, UUID partId) {
        Optional<Order> optOrder = orderRepository.findById(orderId);
        Optional<Part> optPart = partRepository.findById(partId);
        if (optOrder.isEmpty() || optPart.isEmpty()) return;

        Order order = optOrder.get();
        Part part = optPart.get();

        // Находим запись с этим заказом и запчастью
        Optional<RepairPart> rpOpt = repairPartRepository.findByOrderAndPart(order, part);
        rpOpt.ifPresent(repairPartRepository::delete);
    }
}

