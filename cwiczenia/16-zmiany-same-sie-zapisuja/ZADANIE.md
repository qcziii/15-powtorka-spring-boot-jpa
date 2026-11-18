# 16. Podgląd rabatu zmienia cenę w bazie, a edycja kasuje dane

Poziom: **średni (2 błędy)** · Rodzaj: **napraw** · Teoria: [08. Kontekst utrwalania](../../teoria/08-kontekst-utrwalania.md)

## Uruchomienie

```bash
./cwiczenia/16-zmiany-same-sie-zapisuja/check.sh
```

## Zgłoszenie

1. Ekran „symulacja promocji” woła `previewDiscount`, żeby pokazać cenę po rabacie. Nikt nie woła `save`, a cena w bazie spada przy każdym podglądzie.
2. Edycja produktu (`change`) działa jak PATCH: pole `null` znaczy „nie zmieniaj”. Po zmianie samej nazwy produkt traci cenę i stan magazynowy.

**Oczekiwane:** podgląd niczego nie zmienia w bazie; edycja zmienia tylko podane pola.

## Wymagania

- Nie zmieniaj encji `Product` ani testu.

## Wskazówki

- Co Hibernate robi przy końcu transakcji z encjami, które zostały wczytane i zmienione (dirty checking)?
- Co robi `save` (czyli `merge`) z obiektem, który ma ustawione `id`, a pozostałe pola `null`?

## Pytania dodatkowe

1. Jakie stany może mieć encja (transient, managed, detached, removed)? W jakim stanie jest produkt z `findById` w transakcji?
2. Czy w metodzie `change` po poprawce potrzebne jest `save`? Dlaczego?
3. Samo `@Transactional(readOnly = true)` na `previewDiscount` też sprawi, że test przejdzie. Dlaczego zmienianie encji tylko „do obliczeń” i tak jest złym pomysłem?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
