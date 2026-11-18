# 02. Kursy walut się nie wczytują, a walidacja się nie kompiluje

Poziom: **łatwy (2 błędy)** · Rodzaj: **napraw** · Teoria: [01. Maven](../../teoria/01-maven.md)

## Uruchomienie

```bash
./cwiczenia/02-brakujacy-plik-i-zaleznosc/check.sh
```

## Zgłoszenie

Moduł przelewów zagranicznych:

- `TransferValidator` używa Bean Validation (`jakarta.validation`), ale projekt się nie kompiluje.
- `ExchangeRates` czyta kursy z pliku `pl/kurs/review/rates.properties` z classpath. Plik jest w repo, a mimo to po uruchomieniu leci `IllegalStateException: Nie ma pliku ... na classpath`.

**Oczekiwane:** projekt się kompiluje, kursy się wczytują, walidacja wskazuje złe pola.

## Wymagania

- Możesz zmieniać `pom.xml` i przenosić pliki. Nie zmieniaj kodu Javy.
- Nie dopisuj wersji zależności.

## Wskazówki

- Jaki starter Spring Boot dostarcza Bean Validation? Samo API (`jakarta.validation-api`) wystarczy do kompilacji, ale czy wystarczy do uruchomienia? Sprawdź.
- Gdzie Maven szuka plików, które mają trafić na classpath (do `target/classes`)? Zajrzyj do `target/classes/pl/kurs/review/` po kompilacji: czy jest tam plik `.properties`?

## Pytania dodatkowe

1. Co trafia do `target/classes` z `src/main/java`, a co z `src/main/resources`?
2. Czym się różni API (np. `jakarta.validation-api`) od implementacji (np. Hibernate Validator)? Co się stanie, gdy w projekcie jest tylko API?
3. Co to jest starter Spring Boot i dlaczego zwykle dodaje się starter zamiast pojedynczych bibliotek?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
