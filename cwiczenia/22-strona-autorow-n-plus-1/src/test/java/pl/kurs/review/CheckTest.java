package pl.kurs.review;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "spring.jpa.properties.hibernate.generate_statistics=true",
        // Paging "in memory" (all rows from the database, page cut out in Java) is an error here, not a warning.
        "spring.jpa.properties.hibernate.query.fail_on_pagination_over_collection_fetch=true"
})
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    @Autowired
    AuthorListService service;
    @Autowired
    EntityManagerFactory emf;

    private long queriesOf(Runnable action) {
        Statistics stats = emf.unwrap(SessionFactory.class).getStatistics();
        stats.clear();
        action.run();
        return stats.getPrepareStatementCount();
    }

    @Test
    @DisplayName("1) Pierwsza strona: 5 autorów alfabetycznie, z ich książkami; razem 12 autorów, 3 strony")
    void firstPage() {
        AuthorPage page = service.page(0);
        assertEquals(List.of("Adam Mickiewicz", "Andrzej Sapkowski", "Bolesław Prus", "Bruno Schulz", "Czesław Miłosz"),
                page.authors().stream().map(AuthorPage.AuthorRow::name).toList());
        assertEquals(List.of("Krew elfów", "Miecz przeznaczenia", "Ostatnie życzenie"), page.authors().get(1).titles());
        assertEquals(12, page.totalAuthors());
        assertEquals(3, page.totalPages());
    }

    @Test
    @DisplayName("2) Ostatnia strona: 2 autorów")
    void lastPage() {
        AuthorPage page = service.page(2);
        assertEquals(List.of("Wisława Szymborska", "Witold Gombrowicz"),
                page.authors().stream().map(AuthorPage.AuthorRow::name).toList());
        assertEquals(List.of("Ferdydurke", "Trans-Atlantyk"), page.authors().get(1).titles());
    }

    @Test
    @DisplayName("3) Strona to najwyżej 3 zapytania SQL, niezależnie od liczby autorów na stronie")
    void fewQueries() {
        long queries = queriesOf(() -> service.page(0));
        assertTrue(queries <= 3, "Zapytań SQL dla jednej strony: " + queries
                + " (zapytania są w logu: target/surefire-reports/pl.kurs.review.CheckTest-output.txt)");
    }
}
