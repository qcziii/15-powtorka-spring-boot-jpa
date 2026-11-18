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

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    @Autowired
    WebApplicationContext context;
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private String report(String json) throws Exception {
        return mvc.perform(post("/api/reports").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("1) Dwa raporty po kolei: każdy zawiera tylko swoje dane")
    void twoReports() throws Exception {
        String ala = report("""
                {"customer":"Ala","lines":[{"product":"Kawa","quantity":2,"price":30.00}]}""");
        String bartek = report("""
                {"customer":"Bartek","lines":[{"product":"Herbata","quantity":1,"price":12.50}]}""");
        assertEquals("Klient: Ala\nKawa x2 = 60.00\nRazem: 60.00", ala);
        assertEquals("Klient: Bartek\nHerbata x1 = 12.50\nRazem: 12.50", bartek,
                "Raport Bartka zawiera dane z wcześniejszego raportu");
    }

    @Test
    @DisplayName("2) Ten sam raport wywołany dwa razy daje ten sam wynik")
    void sameReportTwice() throws Exception {
        String json = """
                {"customer":"Celina","lines":[{"product":"Kubek","quantity":3,"price":10.00},{"product":"Kawa","quantity":1,"price":30.00}]}""";
        String first = report(json);
        assertEquals("Klient: Celina\nKubek x3 = 30.00\nKawa x1 = 30.00\nRazem: 60.00", first);
        assertEquals(first, report(json));
    }
}
