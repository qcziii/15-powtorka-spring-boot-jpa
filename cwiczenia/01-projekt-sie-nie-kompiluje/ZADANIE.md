# 01. Projekt się nie kompiluje

Poziom: **łatwy (2 błędy)** · Rodzaj: **napraw** · Teoria: [01. Maven](../../teoria/01-maven.md)

## Uruchomienie

```bash
./cwiczenia/01-projekt-sie-nie-kompiluje/check.sh
```

## Zgłoszenie

Kolega skopiował `pom.xml` ze starego projektu. Kod `LabelFormatter` jest dobry (przeszedł review w innym projekcie), ale tutaj nie chce się skompilować: kompilator nie widzi `StringUtils`, a potem nie zna metody `List.getLast()`.

**Oczekiwane:** projekt się kompiluje, testy przechodzą.

## Wymagania

- W tym ćwiczeniu zmieniasz **tylko `pom.xml`** ćwiczenia. Nie zmieniaj kodu Javy.
- Nie dopisuj wersji zależności: wersje zarządza rodzic (`spring-boot-starter-parent`).

## Wskazówki

- Przeczytaj pierwszy błąd kompilacji. Czy biblioteka jest w projekcie? Jeśli tak, to w którym **zakresie** (`scope`) i co ten zakres oznacza dla kodu w `src/main`?
- `List.getLast()` jest w JDK od wersji 21. Skąd Maven bierze wersję Javy, dla której kompiluje? Zobacz `mvn help:effective-pom` (szukaj `maven.compiler.release`).
- `mvn dependency:tree` pokazuje, skąd przychodzi każda zależność i w jakim zakresie.

## Pytania dodatkowe

1. Czym się różnią zakresy `compile`, `runtime`, `test` i `provided`? Podaj przykład zależności do każdego.
2. Dlaczego przy zależności `commons-lang3` nie trzeba pisać wersji? Gdzie jest zapisana?
3. Co dokładnie robi `<java.version>` w projekcie ze `spring-boot-starter-parent`?
4. Jaka jest kolejność faz `validate → compile → test → package → install`? Co uruchomi `mvn package`?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
