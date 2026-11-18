package pl.kurs.review;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.DisplayName.class)
class DevProfileCheckTest {

    @Autowired
    ShippingService shipping;

    @Test
    @DisplayName("4) Profil dev: inna nazwa sklepu")
    void name() {
        assertEquals("Sklep TEST", shipping.shopName());
    }

    @Test
    @DisplayName("5) Profil dev: dostawa darmowa, kraje z application.yml")
    void shipping() {
        assertEquals(0, BigDecimal.ZERO.compareTo(shipping.shippingFor("SK", new BigDecimal("50"))), "Dostawa w profilu dev");
        assertThrows(IllegalArgumentException.class, () -> shipping.shippingFor("DE", BigDecimal.TEN), "Kraje z application.yml");
    }
}
