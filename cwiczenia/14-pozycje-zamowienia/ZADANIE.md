# 14. Pozycje zamówienia nie zapisują się i nie usuwają

Poziom: **łatwy/średni (2 błędy)** · Rodzaj: **napraw** · Teoria: [07. Relacje między encjami](../../teoria/07-relacje.md)

## Uruchomienie

```bash
./cwiczenia/14-pozycje-zamowienia/check.sh
```

## Zgłoszenie

Zamówienie (`PurchaseOrder`) ma pozycje (`OrderLine`). Relacja jest dwukierunkowa i obie strony są ustawiane (`addLine`, `removeLine`). Mimo to:

- po zapisie zamówienia w tabeli `order_line` nic nie ma,
- po dodaniu pierwszej poprawki: pozycja usunięta z zamówienia (`removeProduct`) dalej jest w bazie, bez zamówienia.

**Oczekiwane:** pozycje zapisują się i usuwają razem z zamówieniem; pozycja wyjęta z zamówienia znika z bazy.

## Wymagania

- Zmieniasz tylko mapowanie encji. `OrderService` jest poprawny: nie dopisuj tam zapisywania ani usuwania pozycji przez repozytorium.

## Wskazówki

- `save(order)` zapisuje zamówienie. Kto ma zapisać pozycje? Czym jest **kaskada**?
- Co się dzieje z pozycją, która po `removeLine` ma `order = null`? Kto ma ją usunąć?

## Pytania dodatkowe

1. Czym się różni `CascadeType.REMOVE` od `orphanRemoval = true`?
2. Kiedy `CascadeType.ALL` jest złym pomysłem? (podpowiedź: `@ManyToOne` do produktu z katalogu)
3. Dlaczego bez kaskady usunięcie zamówienia z pozycjami może skończyć się błędem klucza obcego?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
