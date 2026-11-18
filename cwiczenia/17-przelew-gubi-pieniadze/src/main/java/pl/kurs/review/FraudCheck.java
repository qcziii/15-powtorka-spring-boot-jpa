package pl.kurs.review;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Simplified anti-fraud check. This class is correct: do not change it. */
@Component
public class FraudCheck {

    private static final BigDecimal LIMIT = new BigDecimal("10000");

    public void verify(Account source, Account target, BigDecimal amount) throws SuspiciousTransferException {
        if (amount.compareTo(LIMIT) >= 0 && source.getBalance().signum() == 0) {
            throw new SuspiciousTransferException("Przelew całego salda powyżej " + LIMIT + " zł z konta " + source.getNumber());
        }
    }
}
