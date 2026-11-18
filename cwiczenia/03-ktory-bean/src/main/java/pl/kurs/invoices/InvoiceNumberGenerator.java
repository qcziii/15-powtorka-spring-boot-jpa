package pl.kurs.invoices;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/** Invoice numbers: FV/1, FV/2, ... (class taken over from another team). */
@Component
public class InvoiceNumberGenerator {

    private final AtomicInteger counter = new AtomicInteger();

    public String next() {
        return "FV/" + counter.incrementAndGet();
    }
}
