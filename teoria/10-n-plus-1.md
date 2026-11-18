# 10. Problem N+1

Ćwiczenia: [21](../cwiczenia/21-lista-zamowien-n-plus-1/ZADANIE.md), [22](../cwiczenia/22-strona-autorow-n-plus-1/ZADANIE.md), [23](../cwiczenia/23-projekt-biblioteka/ZADANIE.md)

## Na czym polega

```java
List<CustomerOrder> orders = repository.findAll();         // 1 zapytanie: 20 zamówień
for (CustomerOrder o : orders) {
    o.getCustomer().getName();                              // + zapytanie o klienta (o ile nie ma go w kontekście)
    o.getItems().size();                                    // + 1 zapytanie o pozycje KAŻDEGO zamówienia
}
```

1 zapytanie o listę + N zapytań o powiązane dane = **N+1**. Przy 20 wierszach to „tylko” 21-26 zapytań i nikt tego nie zauważa na laptopie. Przy 1000 zamówień na produkcji i opóźnieniu sieci 1 ms to sekunda samego czekania na bazę na jedno żądanie.

N+1 często jest **ukryte**: w mapowaniu na DTO, w `toString`, w serializacji JSON (przy `open-in-view: true`), w pętli w szablonie.

## Jak wykryć

1. **Log SQL**: `logging.level.org.hibernate.SQL: debug`. Powtarzające się to samo zapytanie z innym parametrem = N+1.
2. **Statystyki Hibernate**: `spring.jpa.properties.hibernate.generate_statistics: true` i logger `org.hibernate.session.metrics: debug`: po każdej sesji `... N JDBC statements`.
3. **Test** liczący zapytania (tak robią testy w tym repo):

```java
Statistics stats = emf.unwrap(SessionFactory.class).getStatistics();
stats.clear();
service.summaries();
assertTrue(stats.getPrepareStatementCount() <= 1);
```

## EAGER nie pomaga

`@ManyToOne` jest domyślnie `EAGER`, a mimo to `findAll()` daje N+1. JPQL (`select o from CustomerOrder o`) pobiera tylko zamówienia; Hibernate **potem** dociąga relacje EAGER osobnymi zapytaniami. `EAGER` zmienia tylko „kiedy”, nie „jak”, i dodatkowo ładuje dane tam, gdzie nikt ich nie potrzebuje.

## Rozwiązania

### 1. join fetch (JPQL)

```java
@Query("select o from CustomerOrder o join fetch o.customer left join fetch o.items order by o.id")
List<CustomerOrder> findAllWithCustomerAndItems();
```

Jedno zapytanie z joinami. `left join fetch` dla relacji, która może być pusta. Hibernate 6+ sam usuwa duplikaty rodziców powstałe przez join z kolekcją (wcześniej trzeba było `distinct`).

### 2. @EntityGraph

To samo bez pisania JPQL:

```java
@Override
@EntityGraph(attributePaths = {"customer", "items"})
List<CustomerOrder> findAll();
```

### 3. Batch fetching

Zamiast N zapytań po jednym rodzicu: jedno zapytanie `... where order_id in (?, ?, ?, ...)` na paczkę rodziców.

```java
@BatchSize(size = 50)
@OneToMany(mappedBy = "author")
private List<Book> books;
```

albo globalnie: `spring.jpa.properties.hibernate.default_batch_fetch_size: 50`. Daje 1 + ceil(N/50) zapytań, działa ze stronicowaniem i nie zmienia zapytań w repozytorium.

### 4. Projekcja DTO

Gdy potrzebne są tylko wybrane dane (lista, raport), najtaniej pobrać je od razu:

```java
@Query("""
        select new pl.kurs.review.BookRow(b.id, b.title, b.copies - count(l))
        from Book b left join Loan l on l.book = b and l.returned = false
        group by b.id, b.title, b.copies""")
List<BookRow> rows();
```

Jedno zapytanie, liczenie w bazie, żadnych encji w kontekście.

## Stronicowanie i kolekcje

`join fetch`/`@EntityGraph` kolekcji + `Pageable` gryzą się: `limit 5` w SQL ograniczyłby **wiersze joina**, nie autorów. Hibernate wtedy pobiera **wszystko** i tnie stronę w pamięci, z ostrzeżeniem:

```
HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory
```

Przy dużej tabeli to katastrofa (cała tabela w pamięci przy każdym żądaniu). Ustawienie `hibernate.query.fail_on_pagination_over_collection_fetch=true` zamienia ostrzeżenie w wyjątek.

Dobre rozwiązania:

- strona rodziców zwykłym zapytaniem + **batch fetching** kolekcji (2-3 zapytania),
- dwa kroki: strona samych id (`select a.id from Author a order by a.name`, z `Pageable`), potem `select a from Author a left join fetch a.books where a.id in :ids`.

## Dwie kolekcje naraz

`join fetch` dwóch kolekcji typu `List` (bag): `MultipleBagFetchException`. Zmiana na `Set` „pomaga”, ale tworzy iloczyn kartezjański (10 pozycji × 10 płatności = 100 wierszy na zamówienie). Lepiej: jedna kolekcja przez `join fetch`, druga przez batch fetching albo osobne zapytanie.

## Typowe błędy

- `EAGER` jako „rozwiązanie” N+1.
- `join fetch` kolekcji ze stronicowaniem (stronicowanie w pamięci).
- Kilka kolekcji w jednym `join fetch`.
- `open-in-view: true` i leniwe relacje serializowane do JSON-a: N+1 poza serwisem, niewidoczne w kodzie.
- Brak testu liczącego zapytania: N+1 wraca po pierwszej zmianie mapowania DTO.

## Pytania sprawdzające

1. Skąd nazwa N+1? Jak go wykryć?
2. Dlaczego `FetchType.EAGER` nie rozwiązuje N+1?
3. Czym różni się `join fetch` od zwykłego `join`?
4. Ile zapytań wyśle strona 5 autorów z `@BatchSize(size = 50)` na kolekcji książek?
5. Co oznacza `HHH90003004` i dlaczego to groźne?
6. Kiedy projekcja DTO jest lepsza niż pobieranie encji?
