package pl.kurs.review;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    @Autowired
    ShippingService shipping;

    @Test
    @DisplayName("1) Nazwa sklepu z application.yml")
    void name() {
        assertEquals("Sklep Kursanta", shipping.shopName());
    }

    @Test
    @DisplayName("2) Dostawa 15 zł poniżej progu, darmowa od 200 zł")
    void shipping() {
        assertEquals(0, new BigDecimal("15").compareTo(shipping.shippingFor("PL", new BigDecimal("199.99"))), "Dostawa za 199.99 zł");
        assertEquals(0, BigDecimal.ZERO.compareTo(shipping.shippingFor("CZ", new BigDecimal("200"))), "Dostawa za 200 zł");
    }

    @Test
    @DisplayName("3) Wysyłka tylko do krajów z listy")
    void countries() {
        assertThrows(IllegalArgumentException.class, () -> shipping.shippingFor("DE", BigDecimal.TEN));
    }
}
