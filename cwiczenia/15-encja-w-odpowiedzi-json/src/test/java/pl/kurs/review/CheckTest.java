package pl.kurs.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    @Autowired
    WebApplicationContext context;
    @Autowired
    AuthorRepository authors;
    MockMvc mvc;
    Long lemId;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        authors.deleteAll();
        Author lem = new Author("Stanisław Lem", "Zamawiać nowe wydania dopiero po inwentaryzacji");
        lem.addBook(new Book("Solaris", 1961));
        lem.addBook(new Book("Cyberiada", 1965));
        lemId = authors.save(lem).getId();
    }

    /**
     * Status 200 and no exception; otherwise the test fails and shows the exception thrown during the request
     * (an error in the middle of writing the JSON can leave status 200 and a cut-off response).
     */
    private static ResultMatcher okOrShowError() {
        return result -> {
            int status = result.getResponse().getStatus();
            Exception e = result.getResolvedException();
            if (status != 200 || e != null) {
                fail("Status " + status + ", wyjątek: " + (e == null ? "brak" : e.getClass().getName() + ": " + e.getMessage()));
            }
        };
    }

    @Test
    @DisplayName("1) GET /api/authors/{id}: 200, autor z listą książek")
    void authorWithBooks() throws Exception {
        mvc.perform(get("/api/authors/" + lemId))
                .andExpect(okOrShowError())
                .andExpect(jsonPath("$.id").value(lemId))
                .andExpect(jsonPath("$.name").value("Stanisław Lem"))
                .andExpect(jsonPath("$.books", hasSize(2)))
                .andExpect(jsonPath("$.books[*].title").value(org.hamcrest.Matchers.containsInAnyOrder("Solaris", "Cyberiada")))
                .andExpect(jsonPath("$.books[0].publishedYear").isNumber())
                .andExpect(jsonPath("$.books[0].id").isNumber());
    }

    @Test
    @DisplayName("2) Odpowiedź nie zawiera notatek wewnętrznych ani autora powtórzonego w każdej książce")
    void noInternalData() throws Exception {
        mvc.perform(get("/api/authors/" + lemId))
                .andExpect(okOrShowError())
                .andExpect(jsonPath("$.name").value("Stanisław Lem"))
                .andExpect(jsonPath("$.internalNotes").doesNotExist())
                .andExpect(jsonPath("$.books[0].author").doesNotExist());
    }

    @Test
    @DisplayName("3) Nieistniejący autor: 404")
    void notFound() throws Exception {
        mvc.perform(get("/api/authors/999999")).andExpect(status().isNotFound());
    }
}
