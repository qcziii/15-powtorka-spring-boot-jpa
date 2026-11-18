# 05. Raport zamówienia zawiera dane poprzednich klientów

Poziom: **łatwy** · Rodzaj: **napraw** · Teoria: [02. Spring: IoC i wstrzykiwanie zależności](../../teoria/02-spring-ioc-i-di.md)

## Uruchomienie

```bash
./cwiczenia/05-raport-z-cudzymi-danymi/check.sh
```

## Zgłoszenie

`POST /api/reports` zwraca tekstowy raport zamówienia. Pierwszy raport po starcie aplikacji jest dobry. Każdy kolejny zawiera też linie wszystkich wcześniejszych raportów, a suma rośnie. Na produkcji klient zobaczył zamówienie innego klienta.

**Oczekiwane:** każdy raport zawiera tylko swoje dane, także przy wielu użytkownikach naraz.

## Wymagania

- Nie zmieniaj testu ani formatu raportu.

## Wskazówki

- Ile instancji `ReportService` jest w aplikacji? Ile żądań HTTP korzysta z jednej instancji?
- Zanim „naprawisz” to `@Scope("prototype")`: sprawdź, ile razy tworzony jest kontroler i ile razy wstrzykiwany jest do niego serwis.

## Pytania dodatkowe

1. Jaki jest domyślny zakres (scope) beana w Springu? Co z tego wynika dla pól w serwisie?
2. Dlaczego `@Scope("prototype")` na `ReportService` nie rozwiązuje problemu?
3. Jakie pola **mogą** być w singletonie? (podpowiedź: zależności, stałe, rzeczy bezpieczne wątkowo)
4. Co by się stało przy dwóch równoczesnych żądaniach, gdyby `lines` było czyszczone na początku metody?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
