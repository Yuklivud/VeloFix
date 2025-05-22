package velofix.repository;

import velofix.dto.OrderFilterDto;
import velofix.model.entity.Order;

import java.util.List;

public interface OrderFilterSupport {
    List<Order> findAllWithFilter(OrderFilterDto filter);
}
