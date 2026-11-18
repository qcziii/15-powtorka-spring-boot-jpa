package pl.kurs.review;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import pl.kurs.review.order.CheckoutService;
import pl.kurs.review.order.RefundService;
import pl.kurs.review.payment.PaymentGateway;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    @Autowired
    CheckoutService checkout;
    @Autowired
    RefundService refunds;
    @Autowired
    ApplicationContext context;

    @Test
    @DisplayName("1) Płatność za zamówienie idzie kartą i dostaje numer faktury")
    void payByCard() {
        String result = checkout.pay(new BigDecimal("100"));
        assertTrue(result.matches("CARD-\\d+ / FV/\\d+"), "Wynik płatności: " + result);
    }

    @Test
    @DisplayName("2) Zwrot idzie BLIKiem")
    void refundByBlik() {
        String result = refunds.refund(new BigDecimal("50"));
        assertTrue(result.startsWith("BLIK-"), "Wynik zwrotu: " + result);
    }

    @Test
    @DisplayName("3) Obie bramki płatności są beanami (żadnej nie usunięto)")
    void bothGatewaysExist() {
        assertEquals(2, context.getBeansOfType(PaymentGateway.class).size(),
                "Liczba beanów PaymentGateway w kontekście");
    }
}
