package pl.kurs.review;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {

    private static final BigDecimal LIMIT = new BigDecimal("5000");

    private final ShopOrderRepository orders;
    private final AuditRepository audit;

    public OrderService(ShopOrderRepository orders, AuditRepository audit) {
        this.orders = orders;
        this.audit = audit;
    }

    /** Places an order. Every attempt goes to the audit log, also a rejected one. */
    @Transactional
    public Long place(String customer, BigDecimal amount) {
        recordAttempt("Próba zamówienia: " + customer + ", " + amount + " zł");
        ShopOrder order = orders.save(new ShopOrder(customer, amount));
        if (amount.compareTo(LIMIT) > 0) {
            throw new OrderRejectedException("Kwota powyżej limitu " + LIMIT + " zł");
        }
        return order.getId();
    }

    /** Audit entry in its own, separate transaction: it stays even when the order is rolled back. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordAttempt(String message) {
        audit.save(new AuditEntry(message));
    }

    /** Cancels the order. */
    public void cancel(Long orderId) {
        doCancel(orderId);
    }

    @Transactional
    public void doCancel(Long orderId) {
        ShopOrder order = orders.findById(orderId).orElseThrow();
        order.cancel();
    }
}
