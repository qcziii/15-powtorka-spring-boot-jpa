# 04. Rabat i darmowa dostawa z konfiguracji są ignorowane

Poziom: **łatwy/średni (2 błędy)** · Rodzaj: **napraw** · Teoria: [02. Spring: IoC i wstrzykiwanie zależności](../../teoria/02-spring-ioc-i-di.md), [03. Spring Boot i konfiguracja](../../teoria/03-spring-boot-i-konfiguracja.md)

## Uruchomienie

```bash
./cwiczenia/04-konfiguracja-ignorowana/check.sh
```

## Zgłoszenie

Rabat (`shop.discount.percent`) i próg darmowej dostawy (`shop.shipping.free-from`) są w `application.yml`. Mimo to:

- rabat zawsze wynosi 0%: klient płaci cenę katalogową,
- liczenie dostawy kończy się `NullPointerException`.

**Oczekiwane:** rabat i dostawa liczą się według konfiguracji. Test ustawia rabat na 20% (`@SpringBootTest(properties = ...)`), więc wartości nie mogą być wpisane na sztywno w kodzie.

## Wymagania

- Nie zmieniaj `application.yml` ani testu. Nie wpisuj liczb z konfiguracji do kodu.

## Wskazówki

- Kto tworzy obiekt `DiscountPolicy` w `OrderPricing`? Czy Spring wie o tym obiekcie?
- Czy Spring wstrzykuje wartości do pól `static`? Zajrzyj do logu testu (`target/surefire-reports/*-output.txt`): Spring o tym pisze.

## Pytania dodatkowe

1. Co się dzieje z adnotacjami `@Value`, `@Autowired`, `@Transactional` na obiekcie utworzonym przez `new`?
2. Dlaczego `@Value` na polu `static` jest ignorowane?
3. Czym się różni `@Value` od `@ConfigurationProperties`? Kiedy co wybrać?
4. Jak w teście nadpisać jedną właściwość bez zmiany `application.yml`?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
