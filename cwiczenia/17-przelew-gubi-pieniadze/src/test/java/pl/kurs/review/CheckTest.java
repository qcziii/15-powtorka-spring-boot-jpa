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
    TransferService service;
    @Autowired
    AccountRepository accounts;

    @BeforeEach
    void setUp() {
        accounts.deleteAll();
        accounts.save(new Account("PL-ALA", new BigDecimal("1000.00")));
        accounts.save(new Account("PL-BARTEK", new BigDecimal("200.00")));
        accounts.save(new Account("PL-FIRMA", new BigDecimal("25000.00")));
    }

    private BigDecimal balance(String number) {
        return accounts.findById(number).orElseThrow().getBalance();
    }

    @Test
    @DisplayName("1) Poprawny przelew: pieniądze przechodzą z konta na konto")
    void transfer() throws Exception {
        service.transfer("PL-ALA", "PL-BARTEK", new BigDecimal("300.00"));
        assertEquals(new BigDecimal("700.00"), balance("PL-ALA"));
        assertEquals(new BigDecimal("500.00"), balance("PL-BARTEK"));
    }

    @Test
    @DisplayName("2) Brak środków: wyjątek, salda bez zmian")
    void insufficientFunds() {
        assertThrows(InsufficientFundsException.class,
                () -> service.transfer("PL-BARTEK", "PL-ALA", new BigDecimal("500.00")));
        assertEquals(new BigDecimal("200.00"), balance("PL-BARTEK"));
        assertEquals(new BigDecimal("1000.00"), balance("PL-ALA"));
    }

    @Test
    @DisplayName("3) Przelew na nieistniejące konto: wyjątek i pieniądze NIE znikają z konta nadawcy")
    void unknownTarget() {
        assertThrows(AccountNotFoundException.class,
                () -> service.transfer("PL-ALA", "PL-NIE-MA", new BigDecimal("100.00")));
        assertEquals(new BigDecimal("1000.00"), balance("PL-ALA"), "Saldo nadawcy po nieudanym przelewie");
    }

    @Test
    @DisplayName("4) Podejrzany przelew zablokowany: wyjątek i oba salda bez zmian")
    void suspicious() {
        assertThrows(SuspiciousTransferException.class,
                () -> service.transfer("PL-FIRMA", "PL-BARTEK", new BigDecimal("25000.00")));
        assertEquals(new BigDecimal("25000.00"), balance("PL-FIRMA"), "Saldo nadawcy po zablokowanym przelewie");
        assertEquals(new BigDecimal("200.00"), balance("PL-BARTEK"), "Saldo odbiorcy po zablokowanym przelewie");
    }
}
