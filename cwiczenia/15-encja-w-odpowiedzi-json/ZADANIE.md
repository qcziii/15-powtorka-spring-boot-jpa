# 15. Autor z książkami: błąd przy zamianie na JSON i wyciek danych wewnętrznych

Poziom: **średni** · Rodzaj: **napraw** · Teoria: [07. Relacje między encjami](../../teoria/07-relacje.md), [08. Kontekst utrwalania](../../teoria/08-kontekst-utrwalania.md), [04. REST w Spring MVC](../../teoria/04-rest-w-spring-mvc.md)

## Uruchomienie

```bash
./cwiczenia/15-encja-w-odpowiedzi-json/check.sh
```

## Zgłoszenie

`GET /api/authors/{id}` ma zwrócić autora z książkami:

```json
{"id":1,"name":"Stanisław Lem","books":[{"id":1,"title":"Solaris","publishedYear":1961}]}
```

Kontroler zwraca encję `Author` wprost. Wynik: 500 z wyjątkiem o leniwej kolekcji. Kiedy kolega „naprawił” to pobraniem książek od razu, pojawił się błąd o głębokości JSON-a (`nesting depth`). A dział bezpieczeństwa zauważył, że w odpowiedzi są notatki wewnętrzne bibliotekarzy (`internalNotes`).

**Oczekiwane:** odpowiedź jak wyżej: bez `internalNotes`, bez autora powtórzonego w każdej książce; nieistniejący autor to 404.

## Wymagania

- Nie zmieniaj encji, testu ani `application.yml` (`open-in-view: false` zostaje).

## Wskazówki

- Kiedy Jackson zamienia obiekt na JSON: w transakcji serwisu czy już po niej?
- `Author.books` → `Book.author` → `Author.books` → ... Co z tym zrobi Jackson?
- Co powinno wychodzić z serwisu do kontrolera: encja czy obiekt zrobiony dokładnie pod odpowiedź API?

## Pytania dodatkowe

1. Co to jest `LazyInitializationException` i kiedy leci?
2. Co robi `spring.jpa.open-in-view=true` (domyślne!) i dlaczego często się je wyłącza?
3. Dlaczego zwracanie encji z kontrolera to zły pomysł, nawet gdy „działa”? Podaj trzy powody.
4. Czy `@JsonIgnore` na polach encji to dobre rozwiązanie? Co z innym endpointem, który potrzebuje tych pól?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
