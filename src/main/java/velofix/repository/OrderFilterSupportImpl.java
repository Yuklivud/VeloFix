package velofix.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import velofix.dto.OrderFilterDto;
import velofix.model.entity.Order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class OrderFilterSupportImpl implements OrderFilterSupport {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Order> findAllWithFilter(OrderFilterDto filter) {
        StringBuilder sb = new StringBuilder("SELECT o FROM Order o WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        if (filter.getOrderId() != null) {
            sb.append(" AND o.id = :orderId");
            params.put("orderId", filter.getOrderId());
        }

        if (filter.getCustomerFullName() != null && !filter.getCustomerFullName().isBlank()) {
            sb.append(" AND LOWER(o.customer.fullName) LIKE LOWER(:customerFullName)");
            params.put("customerFullName", "%" + filter.getCustomerFullName().trim() + "%");
        }

        if (filter.getMechanicFullName() != null && !filter.getMechanicFullName().isBlank()) {
            sb.append(" AND o.mechanic IS NOT NULL AND LOWER(o.mechanic.fullName) LIKE LOWER(:mechanicFullName)");
            params.put("mechanicFullName", "%" + filter.getMechanicFullName().trim() + "%");
        }

        if (filter.getCreatedAt() != null) {
            sb.append(" AND DATE(o.createdAt) = :createdAt");
            params.put("createdAt", filter.getCreatedAt());
        }

        if (filter.getBicycleModel() != null) {
            sb.append(" AND o.bicycle.model = :bicycleModel");
            params.put("bicycleModel", filter.getBicycleModel());
        }

        if (filter.getBicycleBrand() != null && !filter.getBicycleBrand().isBlank()) {
            sb.append(" AND LOWER(o.bicycle.brand) LIKE LOWER(:bicycleBrand)");
            params.put("bicycleBrand", "%" + filter.getBicycleBrand().trim() + "%");
        }

        if (filter.getStatus() != null) {
            sb.append(" AND o.status = :status");
            params.put("status", filter.getStatus());
        }

        if (filter.getPaymentStatus() != null) {
            sb.append(" AND o.paymentStatus = :paymentStatus");
            params.put("paymentStatus", filter.getPaymentStatus());
        }

        if (filter.getPaymentType() != null) {
            sb.append(" AND o.paymentType = :paymentType");
            params.put("paymentType", filter.getPaymentType());
        }

        TypedQuery<Order> query = em.createQuery(sb.toString(), Order.class);
        params.forEach(query::setParameter);

        return query.getResultList();
    }


}
