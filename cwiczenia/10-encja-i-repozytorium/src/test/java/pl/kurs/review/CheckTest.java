package pl.kurs.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    @Autowired
    BookRepository books;

    @BeforeEach
    void setUp() {
        books.deleteAll();
        books.saveAll(List.of(
                new Book("Lalka", "Bolesław Prus", "978-83-01", new BigDecimal("39.90"), 1890),
                new Book("Faraon", "Bolesław Prus", "978-83-02", new BigDecimal("45.00"), 1897),
                new Book("Solaris", "Stanisław Lem", "978-83-03", new BigDecimal("29.99"), 1961),
                new Book("Cyberiada", "Stanisław Lem", "978-83-04", new BigDecimal("34.50"), 1965),
                new Book("Bajki robotów", "Stanisław Lem", "978-83-05", new BigDecimal("24.90"), 1964),
                new Book("Quo vadis", "Henryk Sienkiewicz", "978-83-06", new BigDecimal("49.99"), 1896)));
    }

    private static List<String> titles(List<Book> list) {
        return list.stream().map(Book::getTitle).toList();
    }

    @Test
    @DisplayName("1) save nadaje id, findById znajduje książkę")
    void saveAndFind() {
        Book saved = books.save(new Book("Ferdydurke", "Witold Gombrowicz", "978-83-07", new BigDecimal("33.00"), 1937));
        assertNotNull(saved.getId(), "id po save");
        assertEquals("Ferdydurke", books.findById(saved.getId()).orElseThrow().getTitle());
    }

    @Test
    @DisplayName("2) findByAuthor: książki autora")
    void byAuthor() {
        assertEquals(3, books.findByAuthor("Stanisław Lem").size());
    }

    @Test
    @DisplayName("3) findByTitleContainingIgnoreCase: fraza w dowolnym miejscu tytułu, bez względu na wielkość liter")
    void byTitlePhrase() {
        assertEquals(List.of("Bajki robotów"), titles(books.findByTitleContainingIgnoreCase("ROBOT")));
    }

    @Test
    @DisplayName("4) findByPriceLessThanOrderByPriceAsc: tańsze niż podana cena, od najtańszej")
    void cheaperThan() {
        assertEquals(List.of("Bajki robotów", "Solaris", "Cyberiada"),
                titles(books.findByPriceLessThanOrderByPriceAsc(new BigDecimal("39.90"))));
    }

    @Test
    @DisplayName("5) countByPublishedYearGreaterThanEqual i existsByIsbn")
    void countAndExists() {
        assertEquals(3, books.countByPublishedYearGreaterThanEqual(1900));
        assertTrue(books.existsByIsbn("978-83-03"));
        assertFalse(books.existsByIsbn("000"));
    }

    @Test
    @DisplayName("6) @Query (JPQL): autorzy z co najmniej N książkami, alfabetycznie")
    void authorsWithAtLeast() {
        assertEquals(List.of("Bolesław Prus", "Stanisław Lem"), books.authorsWithAtLeast(2));
        assertEquals(List.of("Stanisław Lem"), books.authorsWithAtLeast(3));
    }

    @Test
    @DisplayName("7) ISBN jest unikalny: druga książka z tym samym ISBN nie zapisze się")
    void uniqueIsbn() {
        assertThrows(DataIntegrityViolationException.class,
                () -> books.save(new Book("Kopia", "Ktoś", "978-83-01", BigDecimal.ONE, 2000)));
    }
}
