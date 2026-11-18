# 07. API REST notatek (CRUD)

Poziom: **średni** · Rodzaj: **napisz** · Teoria: [04. REST w Spring MVC](../../teoria/04-rest-w-spring-mvc.md)

## Uruchomienie

```bash
./cwiczenia/07-rest-notatki/check.sh
```

## Zadanie

`NoteService` (gotowy) trzyma notatki w pamięci. Napisz `NoteController` z REST API:

| Metoda i adres | Odpowiedź |
|---|---|
| `GET /api/notes` | 200, lista wszystkich notatek |
| `GET /api/notes?q=fraza` | 200, notatki z frazą w tytule (bez względu na wielkość liter) |
| `GET /api/notes/{id}` | 200 i notatka albo 404 |
| `POST /api/notes` z JSON-em `{"title":"...","content":"..."}` | **201 Created**, nagłówek `Location: .../api/notes/{id}`, utworzona notatka w body |
| `PUT /api/notes/{id}` z JSON-em | 200 i zmieniona notatka albo 404 |
| `DELETE /api/notes/{id}` | **204 No Content** albo 404 |

Notatka w JSON-ie: `{"id":1,"title":"...","content":"..."}`.

## Wymagania

- Nie zmieniaj `NoteService`, `Note`, `NoteRequest` ani testu.
- Serwis wstrzyknij przez konstruktor.

## Wskazówki

- `ResponseEntity` pozwala ustawić status, nagłówki i body. `ResponseEntity.of(optional)` daje 200 albo 404.
- Adres do nagłówka `Location`: `ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri()`.
- Jedna metoda może obsłużyć `GET /api/notes` z parametrem i bez niego (`@RequestParam(required = false)`).

## Pytania dodatkowe

1. Dlaczego `POST` zwraca 201, a nie 200? Po co nagłówek `Location`?
2. Czym się różni `PUT` od `PATCH`?
3. Które z metod `GET`, `POST`, `PUT`, `DELETE` są idempotentne? Co to znaczy w praktyce?
4. Kiedy `@PathVariable`, a kiedy `@RequestParam`?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
