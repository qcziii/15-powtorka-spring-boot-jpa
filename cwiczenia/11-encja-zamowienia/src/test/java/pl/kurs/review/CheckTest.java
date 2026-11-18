package pl.kurs.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    @Autowired
    OrderRepository orders;
    @Autowired
    JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        orders.deleteAll();
    }

    @Test
    @DisplayName("1) Zamówienie zapisuje się w tabeli orders")
    void save() {
        Order order = orders.save(new Order("Ala", new BigDecimal("99.90")));
        assertEquals(1, jdbc.queryForObject("select count(*) from orders where id = ?", Integer.class, order.getId()));
    }

    @Test
    @DisplayName("2) Zapisane zamówienie da się odczytać z bazy (findById, findAll)")
    void read() {
        Long id = orders.save(new Order("Bartek", new BigDecimal("15.00"))).getId();
        Order read = orders.findById(id).orElseThrow();
        assertEquals("Bartek", read.getCustomer());
        assertEquals(0, new BigDecimal("15.00").compareTo(read.getTotal()));
        assertEquals(1, orders.findAll().size());
    }

    @Test
    @DisplayName("3) Status jest w bazie zapisany nazwą ('PAID'), nie numerem")
    void statusAsText() {
        Order order = new Order("Celina", new BigDecimal("50.00"));
        order.pay();
        Long id = orders.save(order).getId();
        assertEquals("PAID", jdbc.queryForObject("select status from orders where id = ?", String.class, id));
        assertEquals(1, orders.findByStatus(OrderStatus.PAID).size());
    }
}
