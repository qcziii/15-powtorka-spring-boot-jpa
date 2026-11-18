# 12. Aplikacja kina nie startuje przez zapytania w repozytorium

Poziom: **łatwy/średni (3 błędy)** · Rodzaj: **napraw** · Teoria: [06. JPA: encje i repozytoria](../../teoria/06-jpa-encje-i-repozytoria.md)

## Uruchomienie

```bash
./cwiczenia/12-zapytania-w-repozytorium/check.sh
```

## Zgłoszenie

`MovieRepository` ma trzy zapytania. Aplikacja nie startuje (najpierw jeden błąd, po jego poprawieniu drugi), a po uruchomieniu wyszukiwanie po reżyserze `nolan` nic nie znajduje, choć w bazie są trzy filmy Christophera Nolana.

**Oczekiwane:** `MovieService` działa jak w komentarzach: film po tytule, filmy reżysera po fragmencie nazwiska bez względu na wielkość liter, filmy od podanego roku od najlepiej ocenianego.

## Wymagania

- Nie zmieniaj encji `Movie`, `data.sql` ani testu. Możesz zmieniać `MovieRepository` i `MovieService`.

## Wskazówki

- Spring Data sprawdza zapytania **przy starcie** aplikacji. Komunikat mówi dokładnie, czego nie znalazł.
- Nazwa w metodzie `findBy...` to nazwa **pola encji**. Nazwa w JPQL po `from` to nazwa **encji**, nie tabeli.

## Pytania dodatkowe

1. Dlaczego błąd w zapytaniu wychodzi przy starcie, a nie przy pierwszym wywołaniu? Co to daje?
2. Jak napisać to samo zapytanie natywnym SQL-em? Kiedy warto (`nativeQuery = true`)?
3. Co robi `IgnoreCase` w nazwie metody? Jaki SQL powstaje (sprawdź w logu)?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
