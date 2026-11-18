# 21. Lista zamówień wysyła do bazy kilkadziesiąt zapytań (N+1)

Poziom: **średni** · Rodzaj: **napraw** · Teoria: [10. Problem N+1](../../teoria/10-n-plus-1.md)

## Uruchomienie

```bash
./cwiczenia/21-lista-zamowien-n-plus-1/check.sh
```

## Zgłoszenie

`OrderListService.summaries()` zwraca listę 20 zamówień z nazwą klienta, liczbą pozycji i sumą. Wynik jest dobry, ale lista ładuje się wolno. W logu jest kilkadziesiąt zapytań SQL na jedno wywołanie.

**Oczekiwane:** ten sam wynik w **jednym** zapytaniu SQL.

## Wymagania

- Nie zmieniaj `data.sql` ani testu. Nie zmieniaj typu relacji na `EAGER` (sprawdź, czy by pomogło: `@ManyToOne` do klienta już jest `EAGER`).
- Wynik ma być taki sam: 20 zamówień, bez powtórzeń.

## Wskazówki

- Policz zapytania w logu (`target/surefire-reports/pl.kurs.review.CheckTest-output.txt`, linie `org.hibernate.SQL`). Z czego bierze się liczba? 1 + ? + ?
- `join fetch` w JPQL albo `@EntityGraph` na metodzie repozytorium.

## Pytania dodatkowe

1. Skąd nazwa „N+1”? Jak go wykryć w logach i w testach?
2. Dlaczego `FetchType.EAGER` nie rozwiązuje N+1 przy `findAll()`?
3. Czy `join fetch` dwóch kolekcji naraz to dobry pomysł? (podpowiedź: `MultipleBagFetchException`, iloczyn kartezjański)
4. Kiedy zamiast encji lepiej od razu pobrać DTO (`select new ...`)?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
