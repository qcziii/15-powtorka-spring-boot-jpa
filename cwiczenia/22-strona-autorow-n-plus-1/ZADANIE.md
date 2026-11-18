# 22. Strona autorów z książkami: zapytanie na każdego autora (N+1 i stronicowanie)

Poziom: **średni/trudny** · Rodzaj: **napraw** · Teoria: [10. Problem N+1](../../teoria/10-n-plus-1.md)

## Uruchomienie

```bash
./cwiczenia/22-strona-autorow-n-plus-1/check.sh
```

## Zgłoszenie

`AuthorListService.page(n)` zwraca stronę 5 autorów (alfabetycznie) z tytułami ich książek i liczbą wszystkich autorów oraz stron. Działa, ale każda strona to 7 zapytań: strona autorów, licznik i osobne zapytanie o książki każdego autora.

**Oczekiwane:** jedna strona to najwyżej 3 zapytania SQL, niezależnie od rozmiaru strony. Wynik bez zmian.

## Wymagania

- Nie zmieniaj testu ani danych. Stronicowanie ma się dziać **w bazie**: test włącza `hibernate.query.fail_on_pagination_over_collection_fetch`, więc rozwiązanie, które pobiera wszystkich autorów z książkami i tnie stronę w pamięci Javy, skończy się wyjątkiem.

## Wskazówki

- Spróbuj najpierw `@EntityGraph(attributePaths = "books")` na `findAll(Pageable)` i przeczytaj wyjątek. Dlaczego `join` z kolekcją i `limit` w SQL się gryzą?
- Hibernate umie pobrać kolekcje wielu encji naraz jednym zapytaniem `... where author_id in (?, ?, ...)`: `@BatchSize` albo `hibernate.default_batch_fetch_size`.
- Inna droga: najpierw strona samych identyfikatorów, potem jedno zapytanie z `join fetch` dla tych id.

## Pytania dodatkowe

1. Co oznacza ostrzeżenie `HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory`? Dlaczego to groźne przy dużej tabeli?
2. Ile zapytań wysyła rozwiązanie z `@BatchSize` i jakie to zapytania?
3. Skąd się bierze zapytanie `select count(...)`? Kiedy Spring Data go nie wysyła?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
