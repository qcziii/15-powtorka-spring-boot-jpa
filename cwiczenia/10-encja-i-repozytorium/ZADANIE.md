# 10. Encja książki i repozytorium Spring Data

Poziom: **średni** · Rodzaj: **napisz** · Teoria: [06. JPA: encje i repozytoria](../../teoria/06-jpa-encje-i-repozytoria.md)

## Uruchomienie

```bash
./cwiczenia/10-encja-i-repozytorium/check.sh
```

## Zadanie

1. Zamień `Book` w encję JPA:
   - `id` generowane przez bazę,
   - `title`, `author`, `isbn` wymagane (`nullable = false`), **`isbn` unikalny**,
   - `price` jako `BigDecimal` z dwoma miejscami po przecinku.
   Zachowaj konstruktor i gettery (test ich używa). Pamiętaj, czego JPA potrzebuje, żeby utworzyć obiekt przy odczycie z bazy.
2. Napisz `BookRepository` (rozszerza `JpaRepository<Book, Long>`) z metodami:

| Metoda | Co zwraca |
|---|---|
| `findByAuthor(String)` | książki autora |
| `findByTitleContainingIgnoreCase(String)` | książki z frazą w tytule, bez względu na wielkość liter |
| `findByPriceLessThanOrderByPriceAsc(BigDecimal)` | książki tańsze niż podana cena, od najtańszej |
| `countByPublishedYearGreaterThanEqual(int)` | liczba książek wydanych w roku N i później |
| `existsByIsbn(String)` | czy jest książka z tym ISBN |
| `authorsWithAtLeast(long)` | **własne zapytanie JPQL** (`@Query`): autorzy, którzy mają co najmniej N książek, alfabetycznie |

Na starcie projekt się nie kompiluje (test używa metod, których jeszcze nie ma). To normalne.

## Wymagania

- Metody od 1 do 5 bez `@Query`: nazwy metod mają wystarczyć Spring Data.
- `authorsWithAtLeast` w JPQL (na encjach, nie na tabelach), z `group by` i `having`.

## Wskazówki

- SQL, który generuje Hibernate, jest w logu: `target/surefire-reports/pl.kurs.review.CheckTest-output.txt`.
- Parametr w JPQL: `:minBooks`. Spring dopasowuje go do parametru metody po nazwie (projekt kompiluje się z `-parameters`).

## Pytania dodatkowe

1. Czym się różni JPQL od SQL? Co jest po `from` w JPQL?
2. Co daje `@GeneratedValue(strategy = GenerationType.IDENTITY)`, a co `SEQUENCE`?
3. Dlaczego encja potrzebuje konstruktora bez argumentów? Czy może być `protected`?
4. Jak sprawdzić, czy `unique = true` naprawdę trafiło do bazy?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
