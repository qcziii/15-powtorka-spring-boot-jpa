package pl.kurs.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    @Autowired
    OrderService service;
    @Autowired
    ShopOrderRepository orders;
    @Autowired
    AuditRepository audit;

    @BeforeEach
    void setUp() {
        orders.deleteAll();
        audit.deleteAll();
    }

    private List<String> auditMessages() {
        return audit.findAll().stream().map(AuditEntry::getMessage).toList();
    }

    @Test
    @DisplayName("1) Poprawne zamówienie: zapisane, wpis w audycie")
    void placed() {
        service.place("Ala", new BigDecimal("100"));
        assertEquals(1, orders.count());
        assertEquals(List.of("Próba zamówienia: Ala, 100 zł"), auditMessages());
    }

    @Test
    @DisplayName("2) Odrzucone zamówienie: nie ma go w bazie, ale wpis w audycie ZOSTAJE")
    void rejectedButAudited() {
        assertThrows(OrderRejectedException.class, () -> service.place("Bartek", new BigDecimal("9000")));
        assertEquals(0, orders.count(), "Odrzucone zamówienie nie może zostać w bazie");
        assertEquals(List.of("Próba zamówienia: Bartek, 9000 zł"), auditMessages(), "Wpisy w audycie");
    }

    @Test
    @DisplayName("3) Anulowanie zmienia status zamówienia w bazie")
    void cancel() {
        Long id = service.place("Celina", new BigDecimal("50"));
        service.cancel(id);
        assertEquals(ShopOrder.Status.CANCELLED, orders.findById(id).orElseThrow().getStatus());
    }
}
