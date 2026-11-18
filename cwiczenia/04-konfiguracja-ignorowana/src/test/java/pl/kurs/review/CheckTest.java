package pl.kurs.review;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "shop.discount.percent=20")
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    @Autowired
    OrderPricing pricing;

    @Test
    @DisplayName("1) Rabat z konfiguracji (w teście 20%) jest naliczany")
    void discount() {
        assertEquals(new BigDecimal("80.00"), pricing.goods(new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("2) Poniżej progu darmowej dostawy doliczana jest dostawa 15 zł")
    void paidShipping() {
        assertEquals(new BigDecimal("95.00"), pricing.total(new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("3) Od 200 zł (po rabacie) dostawa jest darmowa")
    void freeShipping() {
        assertEquals(new BigDecimal("200.00"), pricing.total(new BigDecimal("250.00")));
        assertEquals(new BigDecimal("207.00"), pricing.total(new BigDecimal("240.00")),
                "240 zł po rabacie to 192 zł: poniżej progu, dostawa płatna");
    }
}
