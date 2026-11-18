# 11. Zamówienia nie zapisują się do bazy, a odczyt się wywraca

Poziom: **łatwy/średni (3 błędy)** · Rodzaj: **napraw** · Teoria: [06. JPA: encje i repozytoria](../../teoria/06-jpa-encje-i-repozytoria.md)

## Uruchomienie

```bash
./cwiczenia/11-encja-zamowienia/check.sh
```

## Zgłoszenie

Nowa encja `Order`. Każda operacja na repozytorium kończy się błędem SQL. Po pierwszej poprawce zapis działa, ale odczyt z bazy wywraca się z błędem Hibernate. A analityk z zespołu raportów prosi, żeby status był w bazie zapisany czytelnie: `PAID`, a nie `1`.

**Oczekiwane:**

- tabela zamówień nazywa się `orders` (tak ją czytają raporty i test),
- zamówienie da się zapisać i odczytać,
- kolumna `status` zawiera nazwę statusu (`NEW`, `PAID`, ...).

## Wymagania

- Nie zmieniaj nazwy klasy `Order` ani testu.

## Wskazówki

- Przeczytaj SQL z komunikatu błędu. Jak nazywa się tabela? Czy to słowo coś znaczy w SQL?
- Jak Hibernate tworzy obiekt encji przy odczycie z bazy?
- Jak JPA domyślnie zapisuje enum? Co się stanie ze starymi danymi, gdy ktoś doda nowy status na początek enuma?

## Pytania dodatkowe

1. Jakie inne nazwy encji sprawiają kłopot (np. `User`, `Group`)? Jak sobie z tym radzić?
2. Dlaczego `EnumType.ORDINAL` jest niebezpieczny?
3. W logu jest ostrzeżenie o błędzie przy `create table`. Dlaczego aplikacja mimo to wystartowała?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
