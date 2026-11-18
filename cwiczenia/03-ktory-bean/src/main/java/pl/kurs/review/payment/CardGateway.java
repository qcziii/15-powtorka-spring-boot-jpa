package pl.kurs.review.payment;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

/** Card payments. This is the shop's default payment method. */
@Component
public class CardGateway implements PaymentGateway {

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public String charge(BigDecimal amount) {
        return "CARD-" + counter.incrementAndGet();
    }
}
