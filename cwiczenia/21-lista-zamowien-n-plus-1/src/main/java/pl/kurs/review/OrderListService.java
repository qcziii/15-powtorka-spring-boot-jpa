package pl.kurs.review;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class OrderListService {

    private final CustomerOrderRepository orders;

    public OrderListService(CustomerOrderRepository orders) {
        this.orders = orders;
    }

    /** All orders with the customer, the number of items and the total, sorted by order id. */
    @Transactional(readOnly = true)
    public List<OrderSummary> summaries() {
        return orders.findAll().stream()
                .sorted(Comparator.comparing(CustomerOrder::getId))
                .map(o -> new OrderSummary(
                        o.getId(),
                        o.getCustomer().getName(),
                        o.getItems().size(),
                        o.getItems().stream().map(OrderItem::value).reduce(BigDecimal.ZERO, BigDecimal::add)))
                .toList();
    }
}
