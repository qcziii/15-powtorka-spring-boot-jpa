package pl.kurs.review;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    @Test
    @DisplayName("1) Kursy walut wczytują się z pliku rates.properties")
    void rates() {
        ExchangeRates rates = new ExchangeRates();
        assertEquals(new BigDecimal("4.25"), rates.toPln("EUR"));
        assertEquals(new BigDecimal("3.95"), rates.toPln("USD"));
        assertThrows(IllegalArgumentException.class, () -> rates.toPln("XYZ"));
    }

    @Test
    @DisplayName("2) Poprawne zlecenie przelewu przechodzi walidację")
    void validRequest() {
        assertEquals(Set.of(), new TransferValidator()
                .invalidFields(new TransferRequest("Jan Kowalski", "EUR", new BigDecimal("100"))));
    }

    @Test
    @DisplayName("3) Niepoprawne zlecenie: walidacja wskazuje wszystkie złe pola")
    void invalidRequest() {
        assertEquals(Set.of("amount", "currency", "recipient"), new TransferValidator()
                .invalidFields(new TransferRequest(" ", "euro", new BigDecimal("-5"))));
    }
}
