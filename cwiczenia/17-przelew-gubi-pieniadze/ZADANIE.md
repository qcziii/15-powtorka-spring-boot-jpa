# 17. Nieudany przelew i tak zabiera pieniądze z konta

Poziom: **łatwy/średni (2 błędy)** · Rodzaj: **napraw** · Teoria: [09. Transakcje](../../teoria/09-transakcje.md)

## Uruchomienie

```bash
./cwiczenia/17-przelew-gubi-pieniadze/check.sh
```

## Zgłoszenie

`TransferService.transfer` przelewa pieniądze między kontami. Zgłoszenia klientów:

- przelew na nieistniejące konto kończy się błędem, ale pieniądze znikają z konta nadawcy,
- przelew zablokowany przez kontrolę antyfraudową (`SuspiciousTransferException`) kończy się błędem, a pieniądze i tak przechodzą.

**Oczekiwane:** przelew wykonuje się w całości albo wcale.

## Wymagania

- Nie zmieniaj `FraudCheck`, wyjątków ani testu. Kontrola antyfraudowa zostaje na końcu (potrzebuje sald po operacji).

## Wskazówki

- Ile transakcji jest teraz w jednym przelewie? (każde wywołanie repozytorium bez otaczającej transakcji to osobna transakcja)
- Przy jakich wyjątkach Spring **domyślnie** wycofuje transakcję? Czy `SuspiciousTransferException` do nich należy?

## Pytania dodatkowe

1. Dlaczego w Springu domyślnie tylko wyjątki niekontrolowane (i `Error`) powodują rollback?
2. Czym się różni `rollbackFor` od `noRollbackFor`?
3. Czy po poprawce `accounts.save(...)` jest w ogóle potrzebne?
4. Co by się stało, gdyby `transfer` łapał wyjątek w `try/catch` i tylko go logował?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
