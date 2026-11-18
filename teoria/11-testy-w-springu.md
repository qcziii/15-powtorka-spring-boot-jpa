# 11. Testy w Springu

Dotyczy wszystkich ćwiczeń: każde ma test `CheckTest`. Ten rozdział pomaga je czytać, uruchamiać w IntelliJ i pisać własne.

## Rodzaje testów

| Rodzaj | Co uruchamia | Szybkość | Do czego |
|---|---|---|---|
| jednostkowy (JUnit + Mockito) | jedna klasa, zależności zastąpione atrapami | milisekundy | logika w serwisie, w encji |
| `@SpringBootTest` | **cały kontekst** aplikacji (z bazą H2) | sekundy | integracja: kontroler + serwis + baza |
| slice: `@WebMvcTest`, `@DataJpaTest` | tylko warstwa web albo tylko JPA | pomiędzy | kontrolery z atrapą serwisu; repozytoria |

W Spring Boot 4 adnotacje slice są w osobnych modułach (np. `spring-boot-webmvc-test`, `spring-boot-data-jpa-test` i odpowiadające im startery testowe). Testy w tym repo używają tylko `@SpringBootTest` ze `spring-boot-starter-test`.

## Test jednostkowy

```java
class TransferServiceTest {

    AccountRepository accounts = mock(AccountRepository.class);
    TransferService service = new TransferService(accounts, new FraudCheck());   // konstruktor!

    @Test
    void unknownTarget() {
        when(accounts.findById("A")).thenReturn(Optional.of(new Account("A", new BigDecimal("100"))));
        when(accounts.findById("B")).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> service.transfer("A", "B", BigDecimal.TEN));
    }
}
```

Wstrzykiwanie przez konstruktor sprawia, że taki test nie potrzebuje Springa. Ograniczenie: **nie sprawdzisz transakcji, mapowania JPA ani zapytań**: one działają dopiero w Springu i z bazą.

## @SpringBootTest

```java
@SpringBootTest                                          // cały kontekst, tak jak przy starcie aplikacji
@SpringBootTest(properties = "shop.discount.percent=20") // z nadpisaną właściwością
@ActiveProfiles("dev")                                   // z profilem
class CheckTest {

    @Autowired
    OrderService service;                                // beany wstrzykiwane do testu

    @BeforeEach
    void setUp() {
        repository.deleteAll();                          // stan bazy między testami!
    }
}
```

- Kontekst jest **cache'owany** między klasami testów o tej samej konfiguracji: pierwsza klasa czeka kilka sekund, kolejne korzystają z gotowego kontekstu. Inna konfiguracja (`properties`, profil, `@MockitoBean`) = nowy kontekst.
- Gdy kontekst nie wstanie, **wszystkie** testy klasy padają z `Failed to load ApplicationContext`. Prawdziwa przyczyna jest na końcu łańcucha `Caused by:` (`check.sh` w tym repo ją wyciąga).
- Baza H2 w pamięci żyje razem z kontekstem: dane z jednego testu są widoczne w następnym. Czyść je w `@BeforeEach` albo twórz dane unikalne dla testu.

### @Transactional na teście: uwaga

`@Transactional` na metodzie testowej wycofuje wszystko po teście (wygodne), ale **cały test jest jedną transakcją**: nie zobaczysz problemów z granicami transakcji, `LazyInitializationException` ani tego, że zmiana nie została zapisana. Dlatego testy ćwiczeń z transakcjami **nie są** `@Transactional`: każde wywołanie serwisu ma swoją transakcję, jak w prawdziwej aplikacji.

## MockMvc: testy HTTP bez serwera

```java
@Autowired
WebApplicationContext context;
MockMvc mvc;

@BeforeEach
void setUp() {
    mvc = MockMvcBuilders.webAppContextSetup(context).build();
}

@Test
void createNote() throws Exception {
    mvc.perform(post("/api/notes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"title\":\"Zakupy\"}"))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.title").value("Zakupy"))
            .andExpect(jsonPath("$.items", hasSize(2)));
}
```

- MockMvc przepuszcza żądanie przez cały Spring MVC (mapowanie, konwersja JSON, walidacja, `@RestControllerAdvice`), ale bez sieci i Tomcata.
- `jsonPath`: `$` to korzeń, `$.books[0].title`, `$.books[*].title`, `$.errors.email`.
- Nieobsłużony wyjątek z kontrolera: `perform` rzuca `ServletException` (w prawdziwym serwerze byłoby 500).
- `result.getResolvedException()` pokazuje wyjątek obsłużony przez Springa (np. ten, który dał 500 albo 400).
- Z `@SpringBootTest` można też `@AutoConfigureMockMvc` + `@Autowired MockMvc`.

## Prawdziwy serwer

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiTest {
    @LocalServerPort int port;
    // HttpClient / RestClient na http://localhost:port
}
```

Wolniejsze, ale testuje też serwer, filtry i serializację dokładnie jak na produkcji. Uwaga: test i aplikacja działają wtedy w różnych wątkach, więc `@Transactional` na teście niczego nie wycofa.

## Atrapy beanów w kontekście

```java
@MockitoBean                     // Spring Framework 6.2+ (zastępuje dawne @MockBean)
PaymentGateway gateway;          // w kontekście zamiast prawdziwego beana
```

## Jak czytać test ćwiczenia

1. Nazwa testu (`@DisplayName`) mówi, **co** ma działać.
2. `@SpringBootTest(properties = ...)` mówi, jakie ustawienia nadpisuje test (np. rabat 20%).
3. Asercje z komunikatem (`assertEquals(oczekiwane, faktyczne, "opis")`) mówią, **co** porównano.
4. Logi aplikacji z testu (SQL, ostrzeżenia Springa) są w `target/surefire-reports/<klasa>-output.txt`.

W IntelliJ: zielona strzałka przy klasie lub metodzie testu; prawy przycisk → Debug, żeby zatrzymać się w kodzie aplikacji.

## Pytania sprawdzające

1. Czym różni się test jednostkowy serwisu od `@SpringBootTest`? Czego ten pierwszy nie sprawdzi?
2. Dlaczego drugi test w klasie startuje szybciej niż pierwszy?
3. Gdzie szukać prawdziwej przyczyny `Failed to load ApplicationContext`?
4. Dlaczego `@Transactional` na teście może ukryć błąd w transakcjach aplikacji?
5. Jak sprawdzić w MockMvc status, nagłówek i pole JSON-a?
6. Co zrobić, żeby dane z jednego testu nie psuły drugiego?
