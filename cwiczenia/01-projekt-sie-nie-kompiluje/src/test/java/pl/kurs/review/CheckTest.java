package pl.kurs.review;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    private final LabelFormatter formatter = new LabelFormatter();

    @Test
    @DisplayName("1) Nazwa produktu z wielkiej litery, pusta nazwa to '?'")
    void productName() {
        assertEquals("Kawa ziarnista", formatter.productName("  kawa ziarnista "));
        assertEquals("?", formatter.productName("   "));
        assertEquals("?", formatter.productName(null));
    }

    @Test
    @DisplayName("2) Cena z etykiety to ostatnia cena z historii")
    void currentPrice() {
        assertEquals("39.99 zł", formatter.currentPrice(List.of("45.00", "42.50", "39.99")));
    }

    @Test
    @DisplayName("3) Cała etykieta")
    void label() {
        assertEquals("Herbata | 12.00 zł", formatter.label("herbata", List.of("12.00")));
    }
}
