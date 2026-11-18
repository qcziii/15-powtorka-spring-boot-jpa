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

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    @Autowired
    OrderListService service;
    @Autowired
    EntityManagerFactory emf;

    private Statistics statistics() {
        return emf.unwrap(SessionFactory.class).getStatistics();
    }

    @Test
    @DisplayName("1) Lista ma 20 zamówień po kolei, z klientem, liczbą pozycji i sumą")
    void content() {
        List<OrderSummary> list = service.summaries();
        assertEquals(20, list.size(), "Liczba zamówień na liście");
        assertEquals(List.of(1L, 2L, 3L), list.stream().limit(3).map(OrderSummary::orderId).toList());
        OrderSummary first = list.getFirst();
        assertEquals("Ala Kowalska", first.customer());
        assertEquals(3, first.items());
        assertEquals(0, new BigDecimal("90.50").compareTo(first.total()), "Suma zamówienia 1: " + first.total());
        assertEquals("Ewa Lewandowska", list.get(19).customer());
    }

    @Test
    @DisplayName("2) Cała lista to JEDNO zapytanie SQL (bez N+1)")
    void oneQuery() {
        Statistics stats = statistics();
        stats.clear();
        service.summaries();
        long queries = stats.getPrepareStatementCount();
        assertTrue(queries <= 1, "Zapytań SQL: " + queries + " (zapytania są w logu: target/surefire-reports/pl.kurs.review.CheckTest-output.txt)");
    }
}
