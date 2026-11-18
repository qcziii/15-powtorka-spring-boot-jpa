# Ściąga: Maven, Spring Boot, REST, JPA, transakcje, N+1

## Maven

```bash
mvn test                       # kompilacja + testy
mvn package -DskipTests        # jar bez testów
mvn dependency:tree            # skąd jest biblioteka
mvn help:effective-pom         # pom po złożeniu z rodzicem
```

- Kod w `src/main/java`, pliki na classpath w `src/main/resources` (nie obok klas!).
- Zakresy: `compile` (domyślny), `runtime` (sterownik bazy), `test` (JUnit), `provided`.
- Przy zależnościach zarządzanych przez `spring-boot-starter-parent` **bez `<version>`**.
- `<java.version>21</java.version>` → kompilacja dla Javy 21 i jej API.

## Beany i wstrzykiwanie

```java
@Service
public class OrderService {
    private final PaymentGateway gateway;
    public OrderService(PaymentGateway gateway) { this.gateway = gateway; }   // konstruktor, bez @Autowired
}
```

- Bean = obiekt Springa. `new` = nie bean (brak `@Value`, `@Transactional`...).
- Skanowany jest pakiet klasy `@SpringBootApplication` i podpakiety.
- Dwa beany jednego typu: `@Primary` (domyślny), `@Qualifier("nazwaBeana")` (konkretny), `List<Typ>` (wszystkie).
- Singleton (domyślnie): **żadnego stanu żądania w polach**.
- `@Value` nie działa na polach `static`.

## Konfiguracja

```yaml
shop:
  shipping:
    free-from: 200.00          # wcięcie = klucz shop.shipping.free-from
```

```java
@ConfigurationProperties(prefix = "shop")
public record ShopProperties(String name, Shipping shipping) { ... }
// + @ConfigurationPropertiesScan na klasie aplikacji
@Value("${shop.discount.percent:0}") int percent;     // :0 = domyślna
```

- Profil: plik `application-dev.yml` (myślnik), `--spring.profiles.active=dev`, w teście `@ActiveProfiles("dev")`.
- Siła: test > argumenty > zmienne środowiskowe > `application-{profil}.yml` > `application.yml`.

## REST

```java
@RestController
@RequestMapping("/api/notes")
...
@GetMapping("/{id}")   ResponseEntity<Note> get(@PathVariable long id)          -> ResponseEntity.of(optional)
@GetMapping            List<Note> list(@RequestParam(required = false) String q)
@PostMapping           ResponseEntity<Note> create(@Valid @RequestBody NoteRequest r) -> ResponseEntity.created(uri).body(n)
@DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void delete(...)
```

| | |
|---|---|
| 200 OK | odczyt, zmiana |
| 201 Created + `Location` | utworzenie |
| 204 No Content | usunięcie |
| 400 | złe dane |
| 404 | brak zasobu |
| 409 | konflikt ze stanem |
| 500 | nieobsłużony wyjątek |

`@Controller` ≠ `@RestController`. Bez `@RequestBody` JSON jest ignorowany. Z kontrolera zwracaj **DTO, nie encje**.

## Walidacja i błędy

```java
record UserRequest(@NotBlank String name, @NotBlank @Email String email, @Min(18) int age) { }
register(@Valid @RequestBody UserRequest r)          // bez @Valid nic się nie dzieje

@RestControllerAdvice
class ApiErrorHandler {
    @ExceptionHandler(UserNotFoundException.class)
    ProblemDetail notFound(UserNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
```

`@NotNull` przepuszcza `""`, `@NotBlank` nie. `@Email` przepuszcza `""` i `null`.

## Encja

```java
@Entity @Table(name = "orders")                  // "order" to słowo SQL
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String number;
    @Column(precision = 10, scale = 2) private BigDecimal total;
    @Enumerated(EnumType.STRING) private Status status;
    protected Order() { }                       // wymagany
}
```

## Repozytorium

```java
interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCase(String phrase);    // nazwy PÓL encji
    long countByYearGreaterThanEqual(int year);
    boolean existsByIsbn(String isbn);
    @Query("select b from Book b where b.price < :max order by b.price")   // encje, nie tabele
    List<Book> cheaperThan(BigDecimal max);
}
```

Log SQL: `logging.level.org.hibernate.SQL: debug`.

## Relacje

```java
// rodzic                                                      // dziecko = WŁAŚCICIEL (kolumna post_id)
@OneToMany(mappedBy = "post", cascade = CascadeType.ALL,        @ManyToOne(fetch = FetchType.LAZY)
           orphanRemoval = true)                                private Post post;
private List<Comment> comments = new ArrayList<>();
public void addComment(Comment c) { comments.add(c); c.setPost(this); }    // obie strony!
```

- Bez `mappedBy`: tabela łącząca i pusty `post_id`.
- Kaskada tylko dla „części całości”, nigdy na `@ManyToOne` do współdzielonych danych.
- `orphanRemoval`: wyjęte z listy = usunięte z bazy.
- Wszystko `LAZY`; potrzebne dane pobieraj jawnie.

## Kontekst utrwalania

- Encja z `findById` w transakcji jest **managed**: zmiana pola = `update` przy commit, bez `save`.
- Nie zmieniaj encji „do obliczeń”.
- `save(obiekt z id)` = `merge`: kopiuje **wszystkie** pola, także `null`. Częściowa zmiana: wczytaj i zmień pola.
- Po transakcji encje są detached: leniwe relacje → `LazyInitializationException`.
- `open-in-view: false` → wszystko dla odpowiedzi pobierz w serwisie (DTO).

## Transakcje

```java
@Transactional                                           // RuntimeException/Error -> rollback
@Transactional(rollbackFor = MyCheckedException.class)  // wyjątek kontrolowany też
@Transactional(propagation = Propagation.REQUIRES_NEW)   // osobna, niezależna (audyt)
@Transactional(readOnly = true)                          // flush MANUAL: zmiany GINĄ
```

- Bez `@Transactional`: każde wywołanie repozytorium to osobna transakcja.
- Działa tylko przez **proxy**: nie przez `this.metoda()`, nie na `private`, nie na obiekcie z `new`.
- Wyjątek z wewnętrznej metody `REQUIRED` złapany wyżej → `UnexpectedRollbackException` (rollback-only).
- Klasa `readOnly = true` → metody zapisujące z własnym `@Transactional`.

## N+1

| Sposób | Zapytania | Stronicowanie |
|---|---|---|
| `join fetch` / `@EntityGraph` | 1 | **nie** dla kolekcji (HHH90003004: w pamięci) |
| `@BatchSize` / `default_batch_fetch_size` | 1 + N/rozmiar | tak |
| projekcja DTO (`select new ...`) | 1 | tak |
| `EAGER` | **dalej N+1** | |

Licz zapytania: `generate_statistics: true` + `Statistics.getPrepareStatementCount()`.
