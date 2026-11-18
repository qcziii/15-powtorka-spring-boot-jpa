# 06. JPA: encje i repozytoria

Ćwiczenia: [10](../cwiczenia/10-encja-i-repozytorium/ZADANIE.md), [11](../cwiczenia/11-encja-zamowienia/ZADANIE.md), [12](../cwiczenia/12-zapytania-w-repozytorium/ZADANIE.md)

## Warstwy

- **JDBC**: SQL ręcznie, `ResultSet` ręcznie.
- **JPA** (Jakarta Persistence): specyfikacja mapowania obiektów na tabele (ORM). Adnotacje `jakarta.persistence.*`, `EntityManager`, JPQL.
- **Hibernate**: implementacja JPA (Spring Boot 4: Hibernate 7).
- **Spring Data JPA**: repozytoria-interfejsy, zapytania z nazw metod, stronicowanie.

## Encja

```java
@Entity
@Table(name = "orders")                         // opcjonalnie: inna nazwa tabeli
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String customer;

    @Column(nullable = false, unique = true)
    private String number;

    @Column(precision = 10, scale = 2)          // DECIMAL(10,2): pieniądze w BigDecimal
    private BigDecimal total;

    @Enumerated(EnumType.STRING)                // 'PAID', a nie 1
    private OrderStatus status;

    private LocalDateTime createdAt;            // created_at

    @Transient                                  // nie jest kolumną
    private String cachedLabel;

    protected Order() {                          // WYMAGANY przez JPA (może być protected)
    }

    public Order(String customer, BigDecimal total) { ... }
}
```

Reguły:

- **konstruktor bez argumentów** (co najmniej `protected`): Hibernate tworzy przez niego obiekt przy odczycie, potem wpisuje pola. Brak: `No default constructor for entity`,
- klasa nie może być `final` (Hibernate robi podklasy-proxy do leniwego ładowania),
- nazwy: encja `Order` -> tabela `order`, pole `createdAt` -> kolumna `created_at`. **`order`, `user`, `group`, `key`... to słowa zastrzeżone SQL**: `@Table(name = "orders")`,
- enumy: domyślnie `ORDINAL` (numer stałej). Dodanie stałej w środku enuma po cichu zmienia znaczenie danych w bazie. Zawsze `EnumType.STRING`.

### Generowanie id

| Strategia | Jak | Kiedy `insert` |
|---|---|---|
| `IDENTITY` | kolumna auto-increment | **od razu** przy `persist` (żeby poznać id) |
| `SEQUENCE` (domyślna przy `@GeneratedValue` w H2/PostgreSQL) | sekwencja w bazie | przy flush (zwykle commit), można grupować inserty |
| brak `@GeneratedValue` | id ustawiasz sam | przy flush |

Ma to znaczenie przy transakcjach `readOnly` (ćwiczenie 20).

### ddl-auto

`spring.jpa.hibernate.ddl-auto`: `create-drop` (twórz przy starcie, usuń przy końcu; domyślne dla bazy w pamięci), `create`, `update`, `validate` (sprawdź zgodność), `none`. Na produkcji schemat zakłada się migracjami (Flyway/Liquibase) i ustawia `validate` albo `none`. Uwaga: błąd w `create table` Hibernate tylko **loguje** (WARN), aplikacja startuje dalej, a błąd wychodzi przy pierwszym zapytaniu.

## Repozytorium Spring Data

```java
public interface BookRepository extends JpaRepository<Book, Long> { }
```

Za darmo: `save`, `saveAll`, `findById` (`Optional`), `findAll`, `findAll(Pageable)`, `findAll(Sort)`, `count`, `existsById`, `deleteById`, `delete`, `deleteAll`, `flush`. Implementację tworzy Spring przy starcie.

### Zapytania z nazwy metody (derived queries)

```java
List<Book> findByAuthor(String author);
List<Book> findByTitleContainingIgnoreCase(String phrase);      // like %phrase%, bez wielkości liter
List<Book> findByPriceLessThanOrderByPriceAsc(BigDecimal price);
List<Book> findByAuthorAndPublishedYearBetween(String a, int from, int to);
Optional<Book> findFirstByOrderByPriceDesc();                   // najdroższa
long countByPublishedYearGreaterThanEqual(int year);
boolean existsByIsbn(String isbn);
List<Comment> findByPostId(Long postId);                        // pole post, jego pole id
void deleteByAuthor(String author);                             // wymaga transakcji
```

Słowa: `And`, `Or`, `Between`, `LessThan`, `GreaterThanEqual`, `Like`, `Containing`, `StartingWith`, `IgnoreCase`, `In`, `IsNull`, `True`, `OrderBy...Asc/Desc`, `Top3`/`First3`.

**Nazwy w metodzie to nazwy pól encji** (nie kolumn). Nieistniejące pole: aplikacja nie startuje (`PropertyReferenceException: No property 'name' found for type 'Movie'`). To zaleta: błąd wychodzi od razu.

### @Query (JPQL)

```java
@Query("select b.author from Book b group by b.author having count(b) >= :minBooks order by b.author")
List<String> authorsWithAtLeast(long minBooks);
```

JPQL operuje na **encjach i polach**: `from Book b`, nie `from books`. Nazwa tabeli w JPQL: `UnknownEntityException: Could not resolve root entity 'movies'` (przy starcie). Parametry `:nazwa` są wiązane z parametrami metody po nazwie (Spring Boot kompiluje z `-parameters`; inaczej `@Param("nazwa")`).

Natywny SQL, gdy JPQL nie wystarcza (funkcje bazy):

```java
@Query(value = "select * from books where isbn like ?1", nativeQuery = true)
```

Zapytania modyfikujące: `@Modifying @Query("update ...")` w transakcji.

### Projekcja DTO

```java
@Query("select new pl.kurs.review.BookRow(b.title, b.price) from Book b")
List<BookRow> rows();
```

Pobiera tylko potrzebne kolumny, wynik nie jest encją (nie jest śledzony przez kontekst utrwalania).

## Podgląd SQL

```yaml
logging:
  level:
    org.hibernate.SQL: debug                  # zapytania
    org.hibernate.orm.jdbc.bind: trace        # wartości parametrów
```

Zawsze patrz na SQL, który generuje Hibernate, zanim uznasz, że „działa”.

## Typowe błędy

- Brak konstruktora bez argumentów.
- Encja o nazwie słowa zastrzeżonego SQL.
- `EnumType.ORDINAL` (domyślny).
- `double` na pieniądze zamiast `BigDecimal` z `precision`/`scale`.
- Nazwa kolumny zamiast pola w metodzie repozytorium, nazwa tabeli w JPQL.
- `ddl-auto: update` na produkcji.

## Pytania sprawdzające

1. Czym różni się JPA od Hibernate, a Hibernate od Spring Data JPA?
2. Dlaczego encja potrzebuje konstruktora bez argumentów?
3. Dlaczego `EnumType.ORDINAL` jest niebezpieczny?
4. Kiedy Hibernate wykonuje `insert` przy `IDENTITY`, a kiedy przy `SEQUENCE`?
5. Napisz metodę repozytorium: produkty z kategorii, tańsze niż X, posortowane po nazwie.
6. Co jest po `from` w JPQL? Co w natywnym SQL?
7. Jak zobaczyć SQL generowany przez Hibernate?
