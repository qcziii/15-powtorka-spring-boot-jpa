package pl.kurs.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    @Autowired
    OrderService service;
    @Autowired
    JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        jdbc.update("delete from order_line");
        jdbc.update("delete from purchase_order");
    }

    private Long createOrder() {
        Map<String, Integer> lines = new LinkedHashMap<>();
        lines.put("Kawa", 2);
        lines.put("Herbata", 1);
        lines.put("Kubek", 4);
        return service.create("Ala", lines);
    }

    private List<String> productsInDb() {
        return jdbc.queryForList("select product from order_line order by product", String.class);
    }

    @Test
    @DisplayName("1) Zapis zamówienia zapisuje też jego pozycje")
    void saveWithLines() {
        Long id = createOrder();
        assertEquals(List.of("Herbata", "Kawa", "Kubek"), productsInDb(), "Pozycje w tabeli order_line");
        assertEquals(3, jdbc.queryForObject("select count(*) from order_line where order_id = ?", Integer.class, id));
    }

    @Test
    @DisplayName("2) Pozycja usunięta z zamówienia znika z bazy")
    void removeLine() {
        Long id = createOrder();
        service.removeProduct(id, "Herbata");
        assertEquals(List.of("Kawa", "Kubek"), productsInDb(), "Pozycje w tabeli order_line po usunięciu herbaty");
    }

    @Test
    @DisplayName("3) Usunięcie zamówienia usuwa jego pozycje")
    void deleteOrder() {
        Long id = createOrder();
        service.delete(id);
        assertEquals(0, jdbc.queryForObject("select count(*) from purchase_order", Integer.class), "Zamówienia w bazie");
        assertEquals(List.of(), productsInDb(), "Pozycje w bazie");
    }
}
