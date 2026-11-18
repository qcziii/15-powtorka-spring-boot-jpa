package pl.kurs.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    @Autowired
    WebApplicationContext context;
    @Autowired
    NoteService service;
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        service.deleteAll();
    }

    private long create(String title, String content) {
        return service.create(new NoteRequest(title, content)).id();
    }

    @Test
    @DisplayName("1) POST /api/notes: 201 Created, nagłówek Location i utworzona notatka w odpowiedzi")
    void createNote() throws Exception {
        mvc.perform(post("/api/notes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Zakupy\",\"content\":\"mleko, chleb\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.matchesPattern(".*/api/notes/\\d+")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Zakupy"))
                .andExpect(jsonPath("$.content").value("mleko, chleb"));
    }

    @Test
    @DisplayName("2) GET /api/notes/{id}: 200 i notatka")
    void getNote() throws Exception {
        long id = create("Plan", "nauka JPA");
        mvc.perform(get("/api/notes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Plan"));
    }

    @Test
    @DisplayName("3) GET /api/notes/{id} nieistniejącej notatki: 404 Not Found")
    void getMissing() throws Exception {
        mvc.perform(get("/api/notes/999")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("4) GET /api/notes: wszystkie notatki")
    void list() throws Exception {
        create("A", "1");
        create("B", "2");
        mvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].title").value("B"));
    }

    @Test
    @DisplayName("5) GET /api/notes?q=fraza: tylko notatki z frazą w tytule")
    void search() throws Exception {
        create("Zakupy na weekend", "");
        create("Plan nauki", "");
        create("Lista ZAKUPÓW", "");
        mvc.perform(get("/api/notes").param("q", "zakup"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Zakupy na weekend"))
                .andExpect(jsonPath("$[1].title").value("Lista ZAKUPÓW"));
    }

    @Test
    @DisplayName("6) PUT /api/notes/{id}: 200 i zmieniona notatka; nieistniejąca: 404")
    void update() throws Exception {
        long id = create("Stary", "tekst");
        mvc.perform(put("/api/notes/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Nowy\",\"content\":\"inny tekst\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Nowy"));
        mvc.perform(put("/api/notes/999").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"X\",\"content\":\"Y\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("7) DELETE /api/notes/{id}: 204 No Content, potem 404; drugi DELETE: 404")
    void deleteNote() throws Exception {
        long id = create("Do usunięcia", "");
        mvc.perform(delete("/api/notes/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/notes/" + id)).andExpect(status().isNotFound());
        mvc.perform(delete("/api/notes/" + id)).andExpect(status().isNotFound());
    }
}
