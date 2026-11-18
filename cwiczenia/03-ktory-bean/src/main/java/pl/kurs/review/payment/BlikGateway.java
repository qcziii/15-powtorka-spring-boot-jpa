package pl.kurs.review.payment;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

/** BLIK payments. Refunds always go through BLIK. */
@Component
public class BlikGateway implements PaymentGateway {

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public String charge(BigDecimal amount) {
        return "BLIK-" + counter.incrementAndGet();
    }
}
