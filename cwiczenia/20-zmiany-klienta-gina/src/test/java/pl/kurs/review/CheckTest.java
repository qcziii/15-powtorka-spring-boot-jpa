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
    CustomerService service;
    @Autowired
    CustomerRepository customers;

    @BeforeEach
    void setUp() {
        customers.deleteAll();
        customers.save(new Customer("Ala"));
    }

    private Long ala() {
        return customers.findAll().getFirst().getId();
    }

    @Test
    @DisplayName("1) Odczyt listy klientów")
    void names() {
        assertEquals(List.of("Ala"), service.names());
    }

    @Test
    @DisplayName("2) Rejestracja klienta zapisuje go w bazie")
    void register() {
        service.register("Bartek");
        assertEquals(List.of("Ala", "Bartek"), service.names());
    }

    @Test
    @DisplayName("3) Zmiana nazwy klienta zapisuje się w bazie")
    void rename() {
        service.rename(ala(), "Alicja");
        assertEquals(List.of("Alicja"), service.names());
    }

    @Test
    @DisplayName("4) Oznaczenie klienta jako VIP zapisuje się w bazie")
    void vip() {
        service.markVip(ala());
        assertEquals(List.of("Ala"), service.vipNames());
    }
}
