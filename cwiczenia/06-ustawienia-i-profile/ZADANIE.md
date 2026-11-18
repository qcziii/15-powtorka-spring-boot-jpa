# 06. Ustawienia sklepu z application.yml i profil dev nie działają

Poziom: **średni (3 błędy)** · Rodzaj: **napraw** · Teoria: [03. Spring Boot i konfiguracja](../../teoria/03-spring-boot-i-konfiguracja.md)

## Uruchomienie

```bash
./cwiczenia/06-ustawienia-i-profile/check.sh
```

## Zgłoszenie

Ustawienia sklepu mają być w rekordzie `ShopProperties` (`@ConfigurationProperties(prefix = "shop")`): nazwa, lista krajów wysyłki, cena dostawy i próg darmowej dostawy. W profilu `dev` sklep ma inną nazwę i darmową dostawę, reszta ustawień jak w `application.yml`.

Problemy:

- aplikacja nie startuje: brak beana `ShopProperties`,
- po naprawieniu tego `shipping()` jest `null`,
- profil `dev` niczego nie zmienia.

**Oczekiwane:** testy w obu klasach (`CheckTest`: profil domyślny, `DevProfileCheckTest`: profil `dev`) przechodzą.

## Wymagania

- Nie zmieniaj `ShopProperties`, `ShippingService` ani testów. Zmieniasz konfigurację (pliki YAML i ewentualnie klasę aplikacji).

## Wskazówki

- Sama adnotacja `@ConfigurationProperties` nie robi z klasy beana. Co ją rejestruje?
- W YAML-u wcięcie decyduje o tym, pod jakim kluczem jest wartość. Jaki jest pełny klucz `price` w obecnym pliku?
- Jak musi się nazywać plik, który Spring Boot wczyta tylko w profilu `dev`?

## Pytania dodatkowe

1. Jak Spring Boot łączy `application.yml` z `application-dev.yml`? Co wygrywa przy tym samym kluczu?
2. Dlaczego w YAML-u może być `free-from`, a w rekordzie `freeFrom`? (relaxed binding)
3. Jak włączyć profil przy uruchamianiu aplikacji z jara? A w teście?
4. Co jest wygodniejsze przy kilkunastu ustawieniach: `@Value` czy `@ConfigurationProperties`? Dlaczego?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
