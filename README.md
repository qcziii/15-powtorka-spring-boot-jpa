# Powtórka: Maven, Spring Boot, REST, JPA, transakcje i N+1

Repo do powtórki początku części „Java rozszerzona”: od Mavena i Spring Boota, przez REST i JPA, po transakcje i problem N+1.

Ma dwie części:

1. **Teoria** (`teoria/`): 11 rozdziałów z przykładami, typowymi błędami i pytaniami sprawdzającymi. Do tego [ściąga](SCIAGA.md).
2. **Ćwiczenia** (`cwiczenia/`): 23 zadania z automatycznym sprawdzaniem. W większości dostajesz działającą „prawie” aplikację i zgłoszenie błędu („napraw”), w kilku piszesz kod sam („napisz”). Ostatnie to mały projekt, który łączy wszystko.

Każde ćwiczenie to osobna mała aplikacja Spring Boot 4 z bazą H2 w pamięci: nie potrzebujesz Dockera ani zewnętrznej bazy.

Zalecany sposób pracy: przeczytaj rozdział teorii, odpowiedz sobie na pytania sprawdzające na jego końcu, potem zrób ćwiczenia do tego rozdziału.

## Wymagania

- Java 21+ i Maven 3.9+
- `bash` (na Windows: Git Bash) do uruchamiania `check.sh`
- internet przy pierwszym uruchomieniu (Maven pobiera Spring Boot, Hibernate itd., to chwilę trwa)

## Jak pracować

```bash
# automatyczne sprawdzenie jednego ćwiczenia, z katalogu głównego repo
./cwiczenia/03-ktory-bean/check.sh
```

Przeczytaj `ZADANIE.md` w katalogu ćwiczenia, uruchom `check.sh`, zobacz, które testy nie przechodzą i z jakim komunikatem, znajdź przyczynę, popraw i sprawdź. Ćwiczenie jest rozwiązane, gdy `check.sh` wypisze `WYNIK: wszystkie sprawdzenia przeszły`.

`check.sh` kompiluje ćwiczenie i uruchamia jego testy (`mvn test`, klasa `CheckTest`), a potem wypisuje wynik każdego testu po polsku. Gdy aplikacja nie wystartuje, pokazuje prawdziwą przyczynę (ostatnie `Caused by` z wyjątku Springa).

Przydatne miejsca:

- **logi aplikacji z testu** (SQL, ostrzeżenia Springa): `cwiczenia/NN-.../target/surefire-reports/pl.kurs.review.CheckTest-output.txt`,
- **pełny log Mavena**: `cwiczenia/NN-.../target/check-mvn.log`,
- **test** (`src/test/java/.../CheckTest.java`) to też dokumentacja: jeśli nie wiesz, co dokładnie ma zwracać metoda albo endpoint, przeczytaj test.

Projekt otwierasz w IntelliJ jako projekt Maven: `pom.xml` w katalogu głównym otwiera wszystkie ćwiczenia naraz. Testy możesz uruchamiać zieloną strzałką przy `CheckTest`, także w debuggerze.

## Teoria

| # | Rozdział | Ćwiczenia |
|---|---|---|
| 01 | [Maven](teoria/01-maven.md) (`pom.xml`, zakresy zależności, rodzic i BOM, startery, konwencja katalogów, cykl życia) | 01, 02 |
| 02 | [Spring: IoC i wstrzykiwanie zależności](teoria/02-spring-ioc-i-di.md) (bean, skanowanie, wstrzykiwanie przez konstruktor, `@Primary`/`@Qualifier`, scope singleton) | 03, 04, 05 |
| 03 | [Spring Boot i konfiguracja](teoria/03-spring-boot-i-konfiguracja.md) (autokonfiguracja, `application.yml`, `@Value`, `@ConfigurationProperties`, profile) | 04, 06 |
| 04 | [REST w Spring MVC](teoria/04-rest-w-spring-mvc.md) (`@RestController`, mapowania, `@PathVariable`/`@RequestParam`/`@RequestBody`, statusy, `ResponseEntity`) | 07, 08, 15, 23 |
| 05 | [Walidacja i obsługa błędów](teoria/05-walidacja-i-obsluga-bledow.md) (Bean Validation, `@Valid`, `@RestControllerAdvice`, `ProblemDetail`) | 09, 23 |
| 06 | [JPA: encje i repozytoria](teoria/06-jpa-encje-i-repozytoria.md) (encja, id, enumy, Spring Data, zapytania z nazw metod, JPQL) | 10, 11, 12, 23 |
| 07 | [Relacje między encjami](teoria/07-relacje.md) (`@ManyToOne`/`@OneToMany`, `mappedBy`, kaskady, `orphanRemoval`, LAZY/EAGER, encje a JSON) | 13, 14, 15, 23 |
| 08 | [Kontekst utrwalania](teoria/08-kontekst-utrwalania.md) (stany encji, dirty checking, flush, `save`/`merge`, open-in-view) | 15, 16, 20 |
| 09 | [Transakcje](teoria/09-transakcje.md) (`@Transactional`, rollback, proxy i self-invocation, propagacja, rollback-only, `readOnly`) | 17, 18, 19, 20, 23 |
| 10 | [Problem N+1](teoria/10-n-plus-1.md) (wykrywanie, `join fetch`, `@EntityGraph`, batch fetching, DTO, stronicowanie) | 21, 22, 23 |
| 11 | [Testy w Springu](teoria/11-testy-w-springu.md) (`@SpringBootTest`, MockMvc, czytanie testów ćwiczeń) | wszystkie |

## Ćwiczenia

| # | Ćwiczenie | Rodzaj | Poziom |
|---|---|---|---|
| 01 | [Projekt się nie kompiluje](cwiczenia/01-projekt-sie-nie-kompiluje/ZADANIE.md) | napraw | łatwy (2 błędy) |
| 02 | [Kursy walut się nie wczytują, a walidacja się nie kompiluje](cwiczenia/02-brakujacy-plik-i-zaleznosc/ZADANIE.md) | napraw | łatwy (2 błędy) |
| 03 | [Aplikacja nie startuje: Spring nie wie, który bean wstrzyknąć](cwiczenia/03-ktory-bean/ZADANIE.md) | napraw | łatwy/średni (2 błędy) |
| 04 | [Rabat i darmowa dostawa z konfiguracji są ignorowane](cwiczenia/04-konfiguracja-ignorowana/ZADANIE.md) | napraw | łatwy/średni (2 błędy) |
| 05 | [Raport zamówienia zawiera dane poprzednich klientów](cwiczenia/05-raport-z-cudzymi-danymi/ZADANIE.md) | napraw | łatwy |
| 06 | [Ustawienia sklepu z application.yml i profil dev nie działają](cwiczenia/06-ustawienia-i-profile/ZADANIE.md) | napraw | średni (3 błędy) |
| 07 | [API REST notatek (CRUD)](cwiczenia/07-rest-notatki/ZADANIE.md) | napisz | średni |
| 08 | [Kontroler produktów: puste odpowiedzi, 400 i 500 zamiast danych](cwiczenia/08-kontroler-nie-odpowiada/ZADANIE.md) | napraw | łatwy/średni (4 błędy) |
| 09 | [Rejestracja przepuszcza złe dane, a brak użytkownika to błąd 500](cwiczenia/09-walidacja-i-bledy/ZADANIE.md) | napraw | łatwy/średni (3 błędy) |
| 10 | [Encja książki i repozytorium Spring Data](cwiczenia/10-encja-i-repozytorium/ZADANIE.md) | napisz | średni |
| 11 | [Zamówienia nie zapisują się do bazy, a odczyt się wywraca](cwiczenia/11-encja-zamowienia/ZADANIE.md) | napraw | łatwy/średni (3 błędy) |
| 12 | [Aplikacja kina nie startuje przez zapytania w repozytorium](cwiczenia/12-zapytania-w-repozytorium/ZADANIE.md) | napraw | łatwy/średni (3 błędy) |
| 13 | [Komentarze nie są powiązane z postem w bazie](cwiczenia/13-komentarze-bez-posta/ZADANIE.md) | napraw | średni (2 błędy) |
| 14 | [Pozycje zamówienia nie zapisują się i nie usuwają](cwiczenia/14-pozycje-zamowienia/ZADANIE.md) | napraw | łatwy/średni (2 błędy) |
| 15 | [Autor z książkami: błąd przy zamianie na JSON i wyciek danych wewnętrznych](cwiczenia/15-encja-w-odpowiedzi-json/ZADANIE.md) | napraw | średni |
| 16 | [Podgląd rabatu zmienia cenę w bazie, a edycja kasuje dane](cwiczenia/16-zmiany-same-sie-zapisuja/ZADANIE.md) | napraw | średni (2 błędy) |
| 17 | [Nieudany przelew i tak zabiera pieniądze z konta](cwiczenia/17-przelew-gubi-pieniadze/ZADANIE.md) | napraw | łatwy/średni (2 błędy) |
| 18 | [Audyt znika razem z odrzuconym zamówieniem, a anulowanie nie działa](cwiczenia/18-audyt-i-anulowanie/ZADANIE.md) | napraw | średni (2 błędy) |
| 19 | [Jeden zły wiersz wywraca cały import cennika](cwiczenia/19-import-wywraca-sie-w-calosci/ZADANIE.md) | napraw | średni |
| 20 | [Nowi klienci i zmiany danych klientów znikają](cwiczenia/20-zmiany-klienta-gina/ZADANIE.md) | napraw | łatwy |
| 21 | [Lista zamówień wysyła do bazy kilkadziesiąt zapytań (N+1)](cwiczenia/21-lista-zamowien-n-plus-1/ZADANIE.md) | napraw | średni |
| 22 | [Strona autorów z książkami: zapytanie na każdego autora (N+1 i stronicowanie)](cwiczenia/22-strona-autorow-n-plus-1/ZADANIE.md) | napraw | średni/trudny |
| 23 | [Projekt: API biblioteki](cwiczenia/23-projekt-biblioteka/ZADANIE.md) | napisz | trudny |

## Zasady

- Zmieniasz kod w `src/main` ćwiczenia (w 01 i 02 także `pom.xml`, bo to ćwiczenia z Mavena). Nie zmieniaj testów (`src/test`), `check.sh` ani `scripts/`.
- Wymagania z `ZADANIE.md` są częścią zadania (np. „nie zmieniaj encji”, „bez `EAGER`”).
- Zanim cokolwiek zmienisz, **wyjaśnij sobie, dlaczego to nie działa**: która reguła z teorii jest złamana. Poprawka „na próbę” (dopisywanie adnotacji, aż test przejdzie) niczego nie uczy, a na omówieniu padnie pytanie „dlaczego”.
- Czytaj komunikaty do końca: w Springu prawdziwa przyczyna jest w ostatnim `Caused by`.
- Patrz na SQL w logu. W JPA „działa” nie znaczy „działa dobrze”.
- Po każdym ćwiczeniu odpowiedz na pytania dodatkowe z `ZADANIE.md`.

Wszystkie ćwiczenia naraz (podsumowanie OK/FAIL): `./scripts/check-all.sh` (kilka minut).
