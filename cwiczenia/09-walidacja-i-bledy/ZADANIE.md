# 09. Rejestracja przepuszcza złe dane, a brak użytkownika to błąd 500

Poziom: **łatwy/średni (3 błędy)** · Rodzaj: **napraw** · Teoria: [05. Walidacja i obsługa błędów](../../teoria/05-walidacja-i-obsluga-bledow.md)

## Uruchomienie

```bash
./cwiczenia/09-walidacja-i-bledy/check.sh
```

## Zgłoszenie

API rejestracji użytkowników (`POST /api/users`):

- przyjmuje użytkownika z wiekiem 15 i e-mailem `to-nie-email` (201 zamiast 400),
- przyjmuje puste imię i pusty e-mail,
- `GET /api/users/999` zwraca 500 zamiast 404.

Reguły walidacji są w komentarzu `UserRequest`. Format błędów (`ProblemDetail` z polem `errors`) jest już w `ApiErrorHandler`.

**Oczekiwane:** złe dane to 400 z listą złych pól, zajęty e-mail to 409, brak użytkownika to 404 z opisem.

## Wymagania

- Nie zmieniaj `UserService` ani testu.

## Wskazówki

- Adnotacje walidacji na rekordzie same niczego nie sprawdzają. Co uruchamia walidację argumentu metody kontrolera?
- `@NotNull`, `@NotEmpty`, `@NotBlank`: która przepuszcza `""`, a która `"   "`? Czy `@Email` przepuszcza pusty tekst?
- Jaki wyjątek rzuca `UserService.get`, a jaki obsługuje `ApiErrorHandler`?

## Pytania dodatkowe

1. Czym się różni `@NotNull`, `@NotEmpty` i `@NotBlank`?
2. Jaki wyjątek rzuca Spring, gdy walidacja `@Valid @RequestBody` się nie uda? Jaki status daje domyślnie?
3. Gdyby obsługa 404 była na `RuntimeException`, testy by przeszły. Dlaczego to i tak zły pomysł?
4. Co to jest `ProblemDetail` (RFC 9457)? Jakie ma standardowe pola?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
