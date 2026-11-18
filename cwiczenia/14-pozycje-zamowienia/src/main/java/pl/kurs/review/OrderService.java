package pl.kurs.review;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/** This class is correct: the problem is in the mapping of the entities. */
@Service
public class OrderService {

    private final PurchaseOrderRepository orders;

    public OrderService(PurchaseOrderRepository orders) {
        this.orders = orders;
    }

    /** Creates an order with lines: product -> quantity. */
    @Transactional
    public Long create(String customer, Map<String, Integer> lines) {
        PurchaseOrder order = new PurchaseOrder(customer);
        lines.forEach((product, quantity) -> order.addLine(new OrderLine(product, quantity)));
        return orders.save(order).getId();
    }

    /** Removes the line of the product from the order. */
    @Transactional
    public void removeProduct(Long orderId, String product) {
        PurchaseOrder order = orders.findById(orderId).orElseThrow();
        order.getLines().stream()
                .filter(l -> l.getProduct().equals(product))
                .findFirst()
                .ifPresent(order::removeLine);
    }

    @Transactional
    public void delete(Long orderId) {
        orders.deleteById(orderId);
    }
}
