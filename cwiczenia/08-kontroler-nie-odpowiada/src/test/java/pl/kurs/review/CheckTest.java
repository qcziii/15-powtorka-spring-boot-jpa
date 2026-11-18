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

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
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
    ProductService service;
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        service.deleteAll();
        service.create(new ProductRequest("Kawa ziarnista", "kawa", new BigDecimal("39.99")));
        service.create(new ProductRequest("Zielona herbata", "herbata", new BigDecimal("15.00")));
        service.create(new ProductRequest("Kawa mielona", "kawa", new BigDecimal("29.99")));
    }

    @Test
    @DisplayName("1) GET /api/products?category=kawa: 200 i JSON z produktami kategorii")
    void byCategory() throws Exception {
        mvc.perform(get("/api/products").param("category", "kawa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Kawa ziarnista"));
    }

    @Test
    @DisplayName("2) GET /api/products bez kategorii: wszystkie produkty")
    void all() throws Exception {
        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("3) GET /api/products/{id}: 200 i produkt")
    void byId() throws Exception {
        long id = service.find("herbata").getFirst().id();
        mvc.perform(get("/api/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Zielona herbata"));
    }

    @Test
    @DisplayName("4) POST /api/products z JSON-em: 201 i zapisany produkt")
    void create() throws Exception {
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Kubek\",\"category\":\"akcesoria\",\"price\":25.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Kubek"))
                .andExpect(jsonPath("$.price").value(25.00));
    }
}
