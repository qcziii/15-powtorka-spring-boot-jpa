package pl.kurs.review;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    @Autowired
    MovieService service;

    @Test
    @DisplayName("1) Film po tytule")
    void byTitle() {
        assertEquals("Roman Polański", service.byTitle("Pianista").orElseThrow().getDirector());
        assertTrue(service.byTitle("Nie ma takiego").isEmpty());
    }

    @Test
    @DisplayName("2) Filmy reżysera po fragmencie nazwiska, bez względu na wielkość liter")
    void byDirector() {
        assertEquals(List.of("Incepcja", "Interstellar", "Oppenheimer"), service.byDirector("nolan"));
        assertEquals(List.of("Blade Runner 2049", "Diuna"), service.byDirector("Villeneuve"));
    }

    @Test
    @DisplayName("3) Filmy od 2014 roku, od najlepiej ocenianego")
    void releasedSince() {
        assertEquals(List.of("Interstellar", "Oppenheimer", "Diuna", "Blade Runner 2049"), service.releasedSince(2014));
    }
}
