package pl.kurs.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    @Autowired
    ImportService importer;
    @Autowired
    ProductRepository products;

    @BeforeEach
    void setUp() {
        products.deleteAll();
    }

    @Test
    @DisplayName("1) Same poprawne wiersze: wszystkie zapisane")
    void allValid() {
        ImportResult result = importer.importCsv(List.of("K-1;Kawa;39.99", "H-1;Herbata;15.00"));
        assertEquals(new ImportResult(2, List.of()), result);
        assertEquals(2, products.count());
    }

    @Test
    @DisplayName("2) Jeden zły wiersz: pozostałe zapisane, błąd w wyniku")
    void oneInvalid() {
        ImportResult result = importer.importCsv(List.of("K-1;Kawa;39.99", "H-1;Herbata;abc", "C-1;Czekolada;8.50"));
        assertEquals(new ImportResult(2, List.of("wiersz 2: zła cena: abc")), result);
        assertEquals(2, products.count(), "Produkty w bazie po imporcie");
    }

    @Test
    @DisplayName("3) Powtórzony SKU i zły format: oba odrzucone, reszta zapisana")
    void duplicateAndFormat() {
        ImportResult result = importer.importCsv(List.of("K-1;Kawa;39.99", "K-1;Kawa 2;41.00", "bez średników", "M-1;Mleko;4.20"));
        assertEquals(new ImportResult(2, List.of("wiersz 2: SKU już istnieje: K-1", "wiersz 3: zły format: bez średników")), result);
        assertEquals(2, products.count(), "Produkty w bazie po imporcie");
    }
}
