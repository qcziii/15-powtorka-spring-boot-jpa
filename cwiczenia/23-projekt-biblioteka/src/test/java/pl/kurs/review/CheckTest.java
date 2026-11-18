package pl.kurs.review;

import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The tests use only the HTTP API (JSON), so you can name your classes as you like.
 * Every test creates its own data (unique e-mails), so the order of the tests does not matter.
 */
@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    @Autowired
    WebApplicationContext context;
    @Autowired
    EntityManagerFactory emf;
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    // ---- helpers ----

    private ResultActions postJson(String url, String json) throws Exception {
        return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private static long id(ResultActions result) throws Exception {
        return ((Number) JsonPath.read(result.andReturn().getResponse().getContentAsString(), "$.id")).longValue();
    }

    private long book(String title, int copies) throws Exception {
        return id(postJson("/api/books", "{\"title\":\"" + title + "\",\"author\":\"Autor Testowy\",\"copies\":" + copies + "}")
                .andExpect(status().isCreated()));
    }

    private long reader(String name) throws Exception {
        String email = name.toLowerCase() + "-" + UUID.randomUUID() + "@example.com";
        return id(postJson("/api/readers", "{\"name\":\"" + name + "\",\"email\":\"" + email + "\"}")
                .andExpect(status().isCreated()));
    }

    private ResultActions borrow(long bookId, long readerId) throws Exception {
        return postJson("/api/loans", "{\"bookId\":" + bookId + ",\"readerId\":" + readerId + "}");
    }

    private int available(long bookId) throws Exception {
        String json = mvc.perform(get("/api/books")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        List<Integer> values = JsonPath.read(json, "$[?(@.id == " + bookId + ")].available");
        assertEquals(1, values.size(), "Książka " + bookId + " na liście GET /api/books");
        return values.getFirst();
    }

    // ---- tests ----

    @Test
    @DisplayName("01) POST /api/books: 201 i książka z polami id, title, author, copies, available")
    void createBook() throws Exception {
        postJson("/api/books", "{\"title\":\"Solaris\",\"author\":\"Stanisław Lem\",\"copies\":2}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Solaris"))
                .andExpect(jsonPath("$.author").value("Stanisław Lem"))
                .andExpect(jsonPath("$.copies").value(2))
                .andExpect(jsonPath("$.available").value(2));
    }

    @Test
    @DisplayName("02) POST /api/books z pustym tytułem albo 0 egzemplarzy: 400")
    void invalidBook() throws Exception {
        postJson("/api/books", "{\"title\":\" \",\"author\":\"X\",\"copies\":1}").andExpect(status().isBadRequest());
        postJson("/api/books", "{\"title\":\"X\",\"author\":\"X\",\"copies\":0}").andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("03) POST /api/readers: 201; zły e-mail: 400; zajęty e-mail: 409")
    void readers() throws Exception {
        String email = "ala-" + UUID.randomUUID() + "@example.com";
        postJson("/api/readers", "{\"name\":\"Ala\",\"email\":\"" + email + "\"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value(email));
        postJson("/api/readers", "{\"name\":\"Ala\",\"email\":\"to-nie-email\"}").andExpect(status().isBadRequest());
        postJson("/api/readers", "{\"name\":\"Ala druga\",\"email\":\"" + email + "\"}").andExpect(status().isConflict());
    }

    @Test
    @DisplayName("04) Wypożyczenie: 201, dostępnych egzemplarzy mniej o 1")
    void borrowBook() throws Exception {
        long book = book("Lalka", 2);
        long reader = reader("Bartek");
        borrow(book, reader)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.bookId").value(book))
                .andExpect(jsonPath("$.readerId").value(reader))
                .andExpect(jsonPath("$.title").value("Lalka"))
                .andExpect(jsonPath("$.returned").value(false));
        assertEquals(1, available(book));
    }

    @Test
    @DisplayName("05) Wypożyczenie nieistniejącej książki albo przez nieistniejącego czytelnika: 404")
    void notFound() throws Exception {
        long book = book("Faraon", 1);
        long reader = reader("Celina");
        borrow(999_999, reader).andExpect(status().isNotFound());
        borrow(book, 999_999).andExpect(status().isNotFound());
        assertEquals(1, available(book));
    }

    @Test
    @DisplayName("06) Brak wolnych egzemplarzy: 409 i nic się nie zmienia")
    void noCopies() throws Exception {
        long book = book("Quo vadis", 1);
        borrow(book, reader("Darek")).andExpect(status().isCreated());
        long other = reader("Ewa");
        borrow(book, other).andExpect(status().isConflict());
        assertEquals(0, available(book));
        mvc.perform(get("/api/readers/" + other + "/loans")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("07) Czytelnik może mieć najwyżej 3 aktywne wypożyczenia: czwarte to 409")
    void loanLimit() throws Exception {
        long reader = reader("Filip");
        for (int i = 1; i <= 3; i++) {
            borrow(book("Tom " + i, 1), reader).andExpect(status().isCreated());
        }
        long fourth = book("Tom 4", 1);
        borrow(fourth, reader).andExpect(status().isConflict());
        assertEquals(1, available(fourth), "Odrzucone wypożyczenie nie może zająć egzemplarza");
    }

    @Test
    @DisplayName("08) Zwrot: 200, egzemplarz znów dostępny; drugi zwrot: 409; nieistniejące wypożyczenie: 404")
    void returnBook() throws Exception {
        long book = book("Ferdydurke", 1);
        long loan = id(borrow(book, reader("Grażyna")).andExpect(status().isCreated()));
        mvc.perform(post("/api/loans/" + loan + "/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returned").value(true));
        assertEquals(1, available(book));
        mvc.perform(post("/api/loans/" + loan + "/return")).andExpect(status().isConflict());
        mvc.perform(post("/api/loans/999999/return")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("09) Po zwrocie limit 3 wypożyczeń znów pozwala wypożyczyć")
    void limitAfterReturn() throws Exception {
        long reader = reader("Henryk");
        long firstLoan = id(borrow(book("Część 1", 1), reader));
        borrow(book("Część 2", 1), reader);
        borrow(book("Część 3", 1), reader);
        mvc.perform(post("/api/loans/" + firstLoan + "/return")).andExpect(status().isOk());
        borrow(book("Część 4", 1), reader).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("10) GET /api/readers/{id}/loans: aktywne wypożyczenia czytelnika z tytułami; nieznany czytelnik: 404")
    void readerLoans() throws Exception {
        long reader = reader("Irena");
        borrow(book("Pan Tadeusz", 1), reader);
        long returned = id(borrow(book("Dziady", 1), reader));
        mvc.perform(post("/api/loans/" + returned + "/return"));
        mvc.perform(get("/api/readers/" + reader + "/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Pan Tadeusz"));
        mvc.perform(get("/api/readers/999999/loans")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("11) GET /api/books: liczba zapytań SQL nie rośnie z liczbą książek i wypożyczeń (bez N+1)")
    void noNPlusOne() throws Exception {
        long reader = reader("Jan");
        for (int i = 0; i < 3; i++) {
            borrow(book("Seria A " + i, 2), reader);
        }
        long before = queriesOf("/api/books");
        long other = reader("Kasia");
        for (int i = 0; i < 3; i++) {
            borrow(book("Seria B " + i, 2), other);
        }
        long after = queriesOf("/api/books");
        assertTrue(after <= 3 && after == before,
                "Zapytań SQL dla GET /api/books: " + before + " przed i " + after + " po dodaniu 3 książek z wypożyczeniami (ma być stała liczba, najwyżej 3)");
    }

    private long queriesOf(String url) throws Exception {
        Statistics stats = emf.unwrap(SessionFactory.class).getStatistics();
        stats.clear();
        mvc.perform(get(url)).andExpect(status().isOk());
        return stats.getPrepareStatementCount();
    }
}
