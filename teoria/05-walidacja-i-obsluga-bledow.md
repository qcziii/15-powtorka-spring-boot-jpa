# 05. Walidacja i obsługa błędów

Ćwiczenia: [09](../cwiczenia/09-walidacja-i-bledy/ZADANIE.md), [23](../cwiczenia/23-projekt-biblioteka/ZADANIE.md)

## Bean Validation

Starter: `spring-boot-starter-validation` (API `jakarta.validation` + Hibernate Validator). Reguły to adnotacje na polach DTO:

```java
public record UserRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @Min(18) int age,
        @Size(max = 500) String bio,
        @NotNull @Positive BigDecimal deposit,
        @Pattern(regexp = "[A-Z]{3}") String currency,
        @Valid @NotNull AddressRequest address) {    // @Valid: waliduj też obiekt zagnieżdżony
}
```

| Adnotacja | `null` | `""` | `"  "` |
|---|---|---|---|
| `@NotNull` | błąd | OK | OK |
| `@NotEmpty` | błąd | błąd | OK |
| `@NotBlank` | błąd | błąd | błąd |

Większość adnotacji (`@Email`, `@Size`, `@Pattern`, `@Min`...) **przepuszcza `null`**: pusty e-mail `""` jest dla `@Email` poprawny. Wymagalność to osobna adnotacja (`@NotNull`/`@NotBlank`).

## Uruchomienie walidacji

Adnotacje same niczego nie sprawdzają. W kontrolerze walidację włącza `@Valid` (albo `@Validated`) przy parametrze:

```java
@PostMapping
public User register(@Valid @RequestBody UserRequest request) { ... }
```

Nieudana walidacja `@RequestBody` rzuca `MethodArgumentNotValidException`, domyślnie odpowiedź **400**. Walidacja parametrów metod (`@RequestParam @Min(1) int page`) w Spring 6.1+ działa przez wbudowaną walidację metod i rzuca `HandlerMethodValidationException` (też 400).

Walidacja w serwisie (poza kontrolerem): `@Validated` na klasie + `@Valid` przy parametrze; błąd to `ConstraintViolationException`.

## Obsługa wyjątków: @RestControllerAdvice

Bez obsługi wyjątek z kontrolera to **500**. Własne odpowiedzi daje klasa z `@RestControllerAdvice` i metodami `@ExceptionHandler`:

```java
@RestControllerAdvice
public class ApiErrorHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail notFound(UserNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail invalid(MethodArgumentNotValidException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Niepoprawne dane");
        Map<String, String> errors = new TreeMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> errors.put(f.getField(), f.getDefaultMessage()));
        problem.setProperty("errors", errors);
        return problem;
    }
}
```

- Handler łapie wyjątek danego typu **i jego podklasy**. Przy kilku pasujących wygrywa najbardziej szczegółowy.
- Handler na `RuntimeException` albo `Exception` „działa”, ale zamienia każdy błąd programisty (NPE) w np. 404. Obsługuj konkretne wyjątki domenowe, a nieoczekiwane zostaw jako 500 (i zaloguj).
- `@ControllerAdvice` (bez `Rest`) wymaga `@ResponseBody` na metodach, inaczej wynik to nazwa widoku.
- Alternatywa dla prostych przypadków: `@ResponseStatus(HttpStatus.NOT_FOUND)` na klasie wyjątku albo `throw new ResponseStatusException(HttpStatus.NOT_FOUND, "...")`.

## ProblemDetail (RFC 9457)

Standardowy format błędu, `Content-Type: application/problem+json`:

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Nie ma użytkownika o id 999",
  "instance": "/api/users/999"
}
```

Dodatkowe pola: `problem.setProperty("errors", ...)`. Spring Boot może zwracać `ProblemDetail` także dla swoich błędów: `spring.mvc.problemdetails.enabled=true`.

## Wyjątki domenowe

```java
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long id) {
        super("Nie ma użytkownika o id " + id);
    }
}
```

Serwis rzuca wyjątek mówiący **co się stało w domenie** (nie ma użytkownika, e-mail zajęty, limit przekroczony). Kontroler/handler decyduje, **jaki to status HTTP**. Serwis nie powinien znać kodów HTTP.

| Sytuacja | Status |
|---|---|
| złe dane wejściowe (format, zakres) | 400 |
| zasób nie istnieje | 404 |
| konflikt ze stanem (duplikat, brak egzemplarzy, już zwrócone) | 409 |
| reguła biznesowa przy poprawnych danych | 409 albo 422 (ustal w zespole) |
| nieoczekiwany wyjątek | 500 |

## Typowe błędy

- Brak `@Valid`: adnotacje na DTO są tylko ozdobą.
- `@NotNull` na tekście, który nie może być pusty (`@NotBlank`).
- `@Email` bez `@NotBlank`.
- Handler na złym typie wyjątku (np. `IllegalArgumentException`, gdy serwis rzuca własny wyjątek).
- Jeden handler na `Exception` dla wszystkiego.
- Walidacja tylko po stronie front-endu.

## Pytania sprawdzające

1. Czym się różnią `@NotNull`, `@NotEmpty`, `@NotBlank`?
2. Co uruchamia walidację `@RequestBody`? Jaki wyjątek leci przy błędzie?
3. Jak zwrócić listę złych pól w odpowiedzi 400?
4. Dlaczego handler na `RuntimeException` to zły pomysł?
5. Jaki status dla „e-mail zajęty”, a jaki dla „nie ma użytkownika”?
6. Jak zwalidować obiekt zagnieżdżony w DTO?
