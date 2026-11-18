# 04. REST w Spring MVC

Ćwiczenia: [07](../cwiczenia/07-rest-notatki/ZADANIE.md), [08](../cwiczenia/08-kontroler-nie-odpowiada/ZADANIE.md), [15](../cwiczenia/15-encja-w-odpowiedzi-json/ZADANIE.md)

## REST w pigułce

Zasób ma adres, operacje to metody HTTP, wynik to kod statusu i (zwykle) JSON.

| Operacja | Metoda i adres | Sukces | Typowe błędy |
|---|---|---|---|
| lista | `GET /api/notes` | 200 | |
| jeden | `GET /api/notes/7` | 200 | 404 |
| utworzenie | `POST /api/notes` + body | **201** + nagłówek `Location` | 400, 409 |
| zastąpienie | `PUT /api/notes/7` + body | 200 (albo 204) | 400, 404 |
| zmiana części | `PATCH /api/notes/7` + body | 200 | 400, 404 |
| usunięcie | `DELETE /api/notes/7` | **204** | 404 |

Kody: **2xx** sukces, **4xx** błąd klienta (400 złe dane, 401 nieuwierzytelniony, 403 brak uprawnień, 404 brak zasobu, 409 konflikt ze stanem), **5xx** błąd serwera (500: wyjątek, którego nikt nie obsłużył).

**Idempotentne** (powtórzenie daje ten sam stan): `GET`, `PUT`, `DELETE`. `POST` nie jest.

## Kontroler

```java
@RestController                         // = @Controller + @ResponseBody na każdej metodzie
@RequestMapping("/api/notes")           // wspólny początek adresów
public class NoteController {

    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    @GetMapping                                           // GET /api/notes?q=...
    public List<Note> list(@RequestParam(required = false) String q) { ... }

    @GetMapping("/{id}")                                  // GET /api/notes/7
    public ResponseEntity<Note> get(@PathVariable long id) {
        return ResponseEntity.of(service.findById(id));   // Optional -> 200 albo 404
    }

    @PostMapping
    public ResponseEntity<Note> create(@RequestBody NoteRequest request) {
        Note note = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(note.id()).toUri();
        return ResponseEntity.created(location).body(note); // 201 + Location
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)                 // stały status zamiast ResponseEntity
    public void delete(@PathVariable long id) { ... }
}
```

## Skąd Spring bierze dane

| Adnotacja | Źródło | Przykład |
|---|---|---|
| `@PathVariable` | fragment ścieżki | `/api/products/{id}` |
| `@RequestParam` | parametr zapytania (`?category=kawa`) albo formularza | domyślnie **wymagany**: brak = 400; `required = false` albo `defaultValue` |
| `@RequestBody` | body żądania, JSON -> obiekt (Jackson) | `POST`/`PUT` |
| `@RequestHeader` | nagłówek | `@RequestHeader("X-Request-Id")` |
| (brak adnotacji, obiekt) | **parametry zapytania/formularza** wiązane do pól (`@ModelAttribute`) | JSON z body jest wtedy ignorowany! |

Ostatni wiersz to częsty błąd: `create(ProductRequest request)` bez `@RequestBody` dostaje obiekt z samymi `null`.

## @Controller a @RestController

`@Controller` jest dla aplikacji z widokami (HTML): wartość zwrócona z metody to **nazwa widoku**. Zwrócenie listy z `@Controller` bez `@ResponseBody` kończy się błędem o widoku (albo 404). Wyjątek: `ResponseEntity` zawsze jest zapisywane jako body. W API REST używaj `@RestController`.

## ResponseEntity

```java
ResponseEntity.ok(body)                       // 200
ResponseEntity.status(HttpStatus.CREATED).body(body)
ResponseEntity.created(uri).body(body)        // 201 + Location
ResponseEntity.noContent().build()            // 204
ResponseEntity.notFound().build()             // 404
ResponseEntity.of(optional)                   // 200 z body albo 404
```

## JSON i Jackson

Spring Boot 4 używa Jacksona 3 (pakiet `tools.jackson`). Jackson zamienia obiekty na JSON przez **gettery** (albo komponenty rekordu), a JSON na obiekty przez konstruktor rekordu albo settery.

- Rekordy świetnie nadają się na DTO: `record NoteRequest(String title, String content)`.
- `BigDecimal` trafia do JSON-a jako liczba, `LocalDate` jako `"2026-11-18"`.
- **Nie zwracaj encji JPA z kontrolera** (rozdział 07 i ćwiczenie 15): leniwe relacje, nieskończona rekurencja przy relacjach dwukierunkowych, wyciek pól wewnętrznych, API zależne od schematu bazy.

## Warstwy

```
Controller  (HTTP: adresy, statusy, DTO, walidacja wejścia)
   │ DTO
Service     (logika, transakcje)
   │ encje
Repository  (dostęp do bazy)
```

Kontroler nie powinien zawierać logiki biznesowej ani pracować bezpośrednio na repozytorium przy czymś więcej niż proste odczyty.

## Testowanie (zapowiedź rozdziału 11)

```java
mvc.perform(post("/api/notes").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"A\"}"))
   .andExpect(status().isCreated())
   .andExpect(jsonPath("$.title").value("A"));
```

## Typowe błędy

- `@Controller` zamiast `@RestController`.
- `@RequestParam` tam, gdzie wartość jest w ścieżce (i odwrotnie): 400.
- Brak `@RequestBody`: pola `null`.
- Wymagany `@RequestParam` dla filtra, który ma być opcjonalny.
- 200 zamiast 201/204/404: klient nie wie, co się stało.
- Encja zamiast DTO w odpowiedzi.

## Pytania sprawdzające

1. Jaki status i nagłówek powinien zwrócić `POST`, który utworzył zasób?
2. Czym różni się `@PathVariable` od `@RequestParam`?
3. Co się stanie z JSON-em z body, jeśli parametr metody nie ma `@RequestBody`?
4. Co robi `@ResponseBody`? Gdzie jest w `@RestController`?
5. Jakie metody HTTP są idempotentne?
6. Podaj trzy powody, dla których kontroler nie powinien zwracać encji.
