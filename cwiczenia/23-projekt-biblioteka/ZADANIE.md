# 23. Projekt: API biblioteki

Poziom: **trudny** · Rodzaj: **napisz** · Teoria: [04. REST w Spring MVC](../../teoria/04-rest-w-spring-mvc.md), [05. Walidacja i obsługa błędów](../../teoria/05-walidacja-i-obsluga-bledow.md), [06. JPA: encje i repozytoria](../../teoria/06-jpa-encje-i-repozytoria.md), [07. Relacje między encjami](../../teoria/07-relacje.md), [09. Transakcje](../../teoria/09-transakcje.md), [10. Problem N+1](../../teoria/10-n-plus-1.md)

## Uruchomienie

```bash
./cwiczenia/23-projekt-biblioteka/check.sh
```

## Zadanie

Napisz od zera REST API biblioteki: encje, repozytoria, serwisy, kontrolery, DTO, walidację i obsługę błędów. Gotowe są tylko `LibraryApplication`, `application.yml` i `pom.xml`. Test używa wyłącznie HTTP i JSON-a, więc klasy możesz nazwać i ułożyć po swojemu (w pakiecie `pl.kurs.review` albo jego podpakietach).

### Książki

| Żądanie | Odpowiedź |
|---|---|
| `POST /api/books` `{"title":"Solaris","author":"Stanisław Lem","copies":2}` | 201, `{"id":1,"title":"Solaris","author":"Stanisław Lem","copies":2,"available":2}` |
| ... z pustym tytułem/autorem albo `copies < 1` | 400 |
| `GET /api/books` | 200, lista książek jak wyżej; `available` = `copies` minus aktywne (niezwrócone) wypożyczenia |

### Czytelnicy

| Żądanie | Odpowiedź |
|---|---|
| `POST /api/readers` `{"name":"Ala","email":"ala@example.com"}` | 201, `{"id":1,"name":"Ala","email":"ala@example.com"}` |
| ... z pustym imieniem albo złym e-mailem | 400 |
| ... z e-mailem, który już jest | 409 |

### Wypożyczenia

| Żądanie | Odpowiedź |
|---|---|
| `POST /api/loans` `{"bookId":1,"readerId":1}` | 201, `{"id":1,"bookId":1,"readerId":1,"title":"Solaris","returned":false}` |
| ... nieistniejąca książka albo czytelnik | 404 |
| ... brak wolnych egzemplarzy | 409 |
| ... czytelnik ma już 3 aktywne wypożyczenia | 409 |
| `POST /api/loans/{id}/return` | 200, wypożyczenie z `"returned":true`; drugi raz: 409; nieistniejące: 404 |
| `GET /api/readers/{id}/loans` | 200, aktywne wypożyczenia czytelnika (z tytułem); nieznany czytelnik: 404 |

### Wymagania niefunkcjonalne

- Odrzucone wypożyczenie niczego nie zmienia w bazie.
- `GET /api/books` wysyła stałą liczbę zapytań SQL (najwyżej 3), niezależnie od liczby książek i wypożyczeń.
- Kontrolery nie zwracają encji. Błędy w formacie `ProblemDetail`.
- `open-in-view` jest wyłączone: wszystko, czego potrzebuje odpowiedź, musi być pobrane w serwisie.

## Wskazówki

- Zacznij od encji i relacji: `Loan` ma `@ManyToOne` do książki i do czytelnika. Czy książka potrzebuje listy wypożyczeń?
- Liczba dostępnych egzemplarzy dla wszystkich książek jednym zapytaniem: `left join` + `count` + `group by`, wynik prosto do DTO (`select new ...`).
- Rób małymi krokami: uruchamiaj `check.sh` po każdym endpoincie.

## Pytania dodatkowe

1. Gdzie jest granica transakcji przy wypożyczeniu i dlaczego tam?
2. Dwóch czytelników naraz wypożycza ostatni egzemplarz. Co się stanie w Twoim kodzie? Jak temu zapobiec (blokada, `@Version`, ograniczenie w bazie)?
3. Które zapytania Twojej aplikacji mogą mieć N+1? Jak to sprawdziłeś?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
