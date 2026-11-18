# 03. Aplikacja nie startuje: Spring nie wie, który bean wstrzyknąć

Poziom: **łatwy/średni (2 błędy)** · Rodzaj: **napraw** · Teoria: [02. Spring: IoC i wstrzykiwanie zależności](../../teoria/02-spring-ioc-i-di.md)

## Uruchomienie

```bash
./cwiczenia/03-ktory-bean/check.sh
```

## Zgłoszenie

Sklep ma dwie bramki płatności: `CardGateway` i `BlikGateway`. Po dodaniu BLIKa aplikacja przestała startować. Do tego `InvoiceNumberGenerator` (klasa przejęta od innego zespołu) ma `@Component`, a Spring twierdzi, że takiego beana nie ma.

**Oczekiwane:**

- płatności za zamówienia (`CheckoutService`) idą domyślnie **kartą**,
- zwroty (`RefundService`) idą zawsze **BLIKiem**,
- obie bramki zostają beanami (nie usuwaj żadnej),
- `CheckoutService` dostaje numer faktury z `InvoiceNumberGenerator`.

## Wymagania

- Nie zmieniaj testu ani logiki bramek. Nie twórz bramek przez `new`.

## Wskazówki

- Przeczytaj przyczynę w komunikacie `check.sh` (`NoUniqueBeanDefinitionException`, potem `NoSuchBeanDefinitionException`). Komunikat mówi, którego typu i których beanów dotyczy.
- Które pakiety skanuje `@SpringBootApplication`? W jakim pakiecie jest `ShopApplication`, a w jakim `InvoiceNumberGenerator`?
- „Domyślny” bean i „ten konkretny” bean to dwie różne adnotacje.

## Pytania dodatkowe

1. Czym się różni `@Primary` od `@Qualifier`? Co wygra, gdy użyjesz obu?
2. Jak wstrzyknąć **wszystkie** implementacje interfejsu naraz? (podpowiedź: `List<PaymentGateway>`)
3. Dlaczego wstrzykiwanie przez konstruktor jest lepsze niż `@Autowired` na polu?
4. Jakie są dwa sposoby, żeby Spring zobaczył klasę z pakietu `pl.kurs.invoices`?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
