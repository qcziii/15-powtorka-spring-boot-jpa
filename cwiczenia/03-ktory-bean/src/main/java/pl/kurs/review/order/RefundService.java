package pl.kurs.review.order;

import org.springframework.stereotype.Service;
import pl.kurs.review.payment.PaymentGateway;

import java.math.BigDecimal;

@Service
public class RefundService {

    private final PaymentGateway gateway;

    public RefundService(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    /** Refunds the amount. Refunds always go through BLIK. */
    public String refund(BigDecimal amount) {
        return gateway.charge(amount.negate());
    }
}
