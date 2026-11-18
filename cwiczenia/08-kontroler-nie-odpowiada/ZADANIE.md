# 08. Kontroler produktów: puste odpowiedzi, 400 i 500 zamiast danych

Poziom: **łatwy/średni (4 błędy)** · Rodzaj: **napraw** · Teoria: [04. REST w Spring MVC](../../teoria/04-rest-w-spring-mvc.md)

## Uruchomienie

```bash
./cwiczenia/08-kontroler-nie-odpowiada/check.sh
```

## Zgłoszenie

Front-end zgłasza, że API produktów nie działa:

- `GET /api/products?category=kawa` zwraca 200, ale bez JSON-a (w prawdziwej aplikacji: błąd o widoku albo 404),
- `GET /api/products` bez kategorii: 400 Bad Request,
- `GET /api/products/5`: 400 Bad Request,
- `POST /api/products` z poprawnym JSON-em: 500, bo „produkt bez nazwy albo ceny”.

**Oczekiwane:** wszystkie cztery działają jak w komentarzach w `ProductController`.

## Wymagania

- Zmieniasz tylko `ProductController`. `ProductService` jest poprawny.

## Wskazówki

- Czym się różni `@Controller` od `@RestController`? Co robi Spring z wartością zwróconą z metody w zwykłym `@Controller`?
- Skąd Spring bierze wartość parametru oznaczonego `@RequestParam`, a skąd `@PathVariable`?
- Skąd Spring bierze dane do obiektu `ProductRequest`, gdy parametr nie ma żadnej adnotacji?

## Pytania dodatkowe

1. Co to jest `@ResponseBody` i jak ma się do `@RestController`?
2. Dlaczego w metodzie z `ResponseEntity` zwykły `@Controller` działał, a w metodzie z `List` nie?
3. Jaki status zwraca Spring, gdy brakuje wymaganego `@RequestParam`? A gdy JSON jest niepoprawny?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
