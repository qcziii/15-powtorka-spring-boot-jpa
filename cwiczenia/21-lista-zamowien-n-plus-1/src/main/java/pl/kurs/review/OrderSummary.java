package pl.kurs.review;

import java.math.BigDecimal;

/** One row of the orders list. */
public record OrderSummary(Long orderId, String customer, int items, BigDecimal total) {
}
