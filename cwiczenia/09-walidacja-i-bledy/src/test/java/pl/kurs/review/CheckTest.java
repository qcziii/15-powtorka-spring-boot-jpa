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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    @Autowired
    WebApplicationContext context;
    @Autowired
    UserService service;
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        service.deleteAll();
    }

    private ResultActions register(String json) throws Exception {
        return mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    @DisplayName("1) Poprawna rejestracja: 201")
    void valid() throws Exception {
        register("{\"name\":\"Ala\",\"email\":\"ala@example.com\",\"age\":30}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    @DisplayName("2) Za młody użytkownik i zły e-mail: 400 z listą złych pól")
    void invalidAgeAndEmail() throws Exception {
        register("{\"name\":\"Bartek\",\"email\":\"to-nie-email\",\"age\":15}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.age").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.name").doesNotExist());
    }

    @Test
    @DisplayName("3) Puste imię i pusty e-mail (\"\" i spacje): 400")
    void blankFields() throws Exception {
        register("{\"name\":\"   \",\"email\":\"\",\"age\":20}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    @DisplayName("4) Zajęty e-mail: 409 Conflict")
    void emailTaken() throws Exception {
        register("{\"name\":\"Ala\",\"email\":\"ala@example.com\",\"age\":30}").andExpect(status().isCreated());
        register("{\"name\":\"Ala 2\",\"email\":\"ALA@example.com\",\"age\":31}")
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("5) Nieistniejący użytkownik: 404 z opisem błędu")
    void notFound() throws Exception {
        mvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Nie ma użytkownika o id 999"));
    }
}
