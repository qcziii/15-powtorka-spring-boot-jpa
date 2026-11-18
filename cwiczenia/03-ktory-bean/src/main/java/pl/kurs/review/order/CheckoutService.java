package pl.kurs.review.order;

import org.springframework.stereotype.Service;
import pl.kurs.invoices.InvoiceNumberGenerator;
import pl.kurs.review.payment.PaymentGateway;

import java.math.BigDecimal;

@Service
public class CheckoutService {

    private final PaymentGateway gateway;
    private final InvoiceNumberGenerator invoiceNumbers;

    public CheckoutService(PaymentGateway gateway, InvoiceNumberGenerator invoiceNumbers) {
        this.gateway = gateway;
        this.invoiceNumbers = invoiceNumbers;
    }

    /** Pays for the order with the default payment method. Returns "operation id / invoice number". */
    public String pay(BigDecimal amount) {
        return gateway.charge(amount) + " / " + invoiceNumbers.next();
    }
}
