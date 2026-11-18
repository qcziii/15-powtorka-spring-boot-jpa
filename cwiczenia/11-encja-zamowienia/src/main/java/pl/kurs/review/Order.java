package pl.kurs.review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Shop order. */
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customer;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Order(String customer, BigDecimal total) {
        this.customer = customer;
        this.total = total;
        this.status = OrderStatus.NEW;
        this.createdAt = LocalDateTime.now();
    }

    public void pay() {
        if (status != OrderStatus.NEW) {
            throw new IllegalStateException("Można opłacić tylko nowe zamówienie, a to ma status " + status);
        }
        status = OrderStatus.PAID;
    }

    public Long getId() {
        return id;
    }

    public String getCustomer() {
        return customer;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
