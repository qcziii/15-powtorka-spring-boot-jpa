# 19. Jeden zły wiersz wywraca cały import cennika

Poziom: **średni** · Rodzaj: **napraw** · Teoria: [09. Transakcje](../../teoria/09-transakcje.md)

## Uruchomienie

```bash
./cwiczenia/19-import-wywraca-sie-w-calosci/check.sh
```

## Zgłoszenie

Import cennika (`ImportService.importCsv`) ma zapisać poprawne wiersze, a złe pominąć i zgłosić w wyniku (`"wiersz 2: zła cena: abc"`). Kod łapie `InvalidRowException` i idzie dalej. Mimo to przy jednym złym wierszu cały import kończy się wyjątkiem:

```
UnexpectedRollbackException: Transaction silently rolled back because it has been marked as rollback-only
```

i w bazie nie ma żadnego produktu.

**Oczekiwane:** poprawne wiersze zapisane, złe opisane w `ImportResult`, bez wyjątku.

## Wymagania

- Nie zmieniaj testu ani formatu komunikatów. Import ma dalej działać w transakcji (`importCsv` zostaje `@Transactional`).

## Wskazówki

- `ProductService.createFromCsv` też ma `@Transactional`. Z jaką propagacją? Czy to nowa transakcja, czy ta sama?
- Co robi proxy transakcyjne, gdy z metody wylatuje `RuntimeException`, nawet jeśli wyżej ktoś go złapie?

## Pytania dodatkowe

1. Co oznacza „rollback-only”? Kto ustawia tę flagę, a kto ją sprawdza?
2. Podaj trzy różne sposoby naprawy. Który zmienia zachowanie importu (np. co się dzieje przy błędzie bazy w połowie)?
3. Dlaczego ten sam kod bez `@Transactional` na `createFromCsv` działa?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
