package pl.kurs.review;

import org.junit.jupiter.api.BeforeEach;
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
    ProductService service;
    @Autowired
    ProductRepository products;

    @BeforeEach
    void setUp() {
        products.deleteAll();
        products.save(new Product(1L, "Kawa", new BigDecimal("40.00"), 10));
    }

    private Product fromDb() {
        return products.findById(1L).orElseThrow();
    }

    @Test
    @DisplayName("1) Podgląd rabatu zwraca cenę po rabacie")
    void preview() {
        assertEquals(new BigDecimal("30.00"), service.previewDiscount(1L, 25));
    }

    @Test
    @DisplayName("2) Podgląd rabatu NIE zmienia ceny w bazie")
    void previewDoesNotChangeDb() {
        service.previewDiscount(1L, 25);
        service.previewDiscount(1L, 25);
        assertEquals(new BigDecimal("40.00"), fromDb().getPrice(), "Cena w bazie po dwóch podglądach");
    }

    @Test
    @DisplayName("3) Zmiana samej nazwy nie kasuje ceny i stanu")
    void changeNameOnly() {
        service.change(1L, new ProductChange("Kawa ziarnista", null, null));
        Product p = fromDb();
        assertEquals("Kawa ziarnista", p.getName());
        assertEquals(new BigDecimal("40.00"), p.getPrice(), "Cena po zmianie nazwy");
        assertEquals(10, p.getStock(), "Stan po zmianie nazwy");
    }

    @Test
    @DisplayName("4) Zmiana ceny i stanu nie kasuje nazwy; nieistniejący produkt to wyjątek")
    void changePriceAndStock() {
        service.change(1L, new ProductChange(null, new BigDecimal("35.00"), 7));
        Product p = fromDb();
        assertEquals("Kawa", p.getName(), "Nazwa po zmianie ceny");
        assertEquals(new BigDecimal("35.00"), p.getPrice());
        assertEquals(7, p.getStock());
        assertThrows(IllegalArgumentException.class, () -> service.change(99L, new ProductChange("X", null, null)));
    }
}
