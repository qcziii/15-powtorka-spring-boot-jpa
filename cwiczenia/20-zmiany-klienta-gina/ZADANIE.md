# 20. Nowi klienci i zmiany danych klientów znikają

Poziom: **łatwy** · Rodzaj: **napraw** · Teoria: [09. Transakcje](../../teoria/09-transakcje.md), [08. Kontekst utrwalania](../../teoria/08-kontekst-utrwalania.md)

## Uruchomienie

```bash
./cwiczenia/20-zmiany-klienta-gina/check.sh
```

## Zgłoszenie

W `CustomerService` „większość metod tylko czyta”, więc klasa ma `@Transactional(readOnly = true)`. Odczyt działa. Ale:

- nowy klient po `register` nie pojawia się na liście,
- zmiana nazwy (`rename`) i oznaczenie jako VIP (`markVip`) nie zapisują się,
- nigdzie nie ma żadnego wyjątku.

**Oczekiwane:** zapis, zmiana nazwy i oznaczenie VIP trafiają do bazy, odczyt dalej jest read-only.

## Wymagania

- Nie zmieniaj encji ani testu. Nie usuwaj `readOnly` z klasy.

## Wskazówki

- `@Transactional` na klasie dotyczy wszystkich publicznych metod. Co Hibernate robi z flushowaniem w transakcji `readOnly`?
- Dlaczego nawet `save` nie zapisuje? Jak `Customer` dostaje `id` (`@GeneratedValue` bez strategii, czyli sekwencja) i kiedy przy sekwencji wykonuje się `insert`?

## Pytania dodatkowe

1. Co daje `readOnly = true` (Spring, Hibernate, baza)? Kiedy warto je ustawiać?
2. Jak adnotacja na metodzie ma się do adnotacji na klasie?
3. Dlaczego przy `GenerationType.IDENTITY` nowy klient by się zapisał mimo `readOnly`?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
