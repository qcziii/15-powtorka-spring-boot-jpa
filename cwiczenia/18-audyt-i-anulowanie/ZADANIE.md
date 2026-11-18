# 18. Audyt znika razem z odrzuconym zamówieniem, a anulowanie nie działa

Poziom: **średni (2 błędy)** · Rodzaj: **napraw** · Teoria: [09. Transakcje](../../teoria/09-transakcje.md)

## Uruchomienie

```bash
./cwiczenia/18-audyt-i-anulowanie/check.sh
```

## Zgłoszenie

1. Każda próba złożenia zamówienia ma trafić do audytu, także odrzucona. Metoda `recordAttempt` ma `REQUIRES_NEW`, „żeby wpis był w osobnej transakcji”. Mimo to po odrzuceniu zamówienia (kwota powyżej limitu) wpisu w audycie nie ma.
2. `cancel` woła `doCancel`, który ma `@Transactional`. Status zamówienia w bazie się nie zmienia.

**Oczekiwane:** odrzucone zamówienie nie zostaje w bazie, ale wpis w audycie zostaje; anulowanie zmienia status w bazie.

## Wymagania

- Nie zmieniaj encji ani testu. Możesz dodawać klasy.

## Wskazówki

- Jak działa `@Transactional`: kto zaczyna transakcję, gdy wołasz metodę beana z **innego** beana, a kto, gdy wołasz ją przez `this`?
- Pisząc `recordAttempt(...)` w metodzie `place`, wołasz metodę obiektu czy proxy?

## Pytania dodatkowe

1. Co to jest proxy Springa i kiedy jest pomijane?
2. Jakie są sposoby obejścia self-invocation? Który jest najczytelniejszy?
3. Czym się różni `REQUIRED` od `REQUIRES_NEW`? Co się dzieje z transakcją zewnętrzną, gdy wewnętrzna z `REQUIRES_NEW` się wywróci?
4. Dlaczego `@Transactional` na metodzie `private` nie działa?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
