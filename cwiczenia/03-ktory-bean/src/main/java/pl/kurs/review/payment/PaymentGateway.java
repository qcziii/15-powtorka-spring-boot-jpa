package pl.kurs.review.payment;

import java.math.BigDecimal;

public interface PaymentGateway {

    /** Charges (or refunds, for a negative amount) and returns the operation id, e.g. "CARD-1". */
    String charge(BigDecimal amount);
}
