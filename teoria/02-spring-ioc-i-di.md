# 02. Spring: IoC i wstrzykiwanie zależności

Ćwiczenia: [03](../cwiczenia/03-ktory-bean/ZADANIE.md), [04](../cwiczenia/04-konfiguracja-ignorowana/ZADANIE.md), [05](../cwiczenia/05-raport-z-cudzymi-danymi/ZADANIE.md)

## Odwrócenie sterowania (IoC)

Bez Springa obiekt sam tworzy swoje zależności:

```java
public class OrderService {
    private final PaymentGateway gateway = new CardGateway();   // na sztywno
}
```

Ze Springiem obiekt tylko **deklaruje**, czego potrzebuje, a tworzy i łączy obiekty **kontener** (`ApplicationContext`):

```java
@Service
public class OrderService {
    private final PaymentGateway gateway;

    public OrderService(PaymentGateway gateway) {   // Spring poda gotowy bean
        this.gateway = gateway;
    }
}
```

Korzyści: łatwa podmiana implementacji (w testach: atrapa), jedna konfiguracja w jednym miejscu, i to, że Spring może „opakować” obiekt dodatkowym zachowaniem (transakcje, cache, bezpieczeństwo).

## Bean

**Bean** to obiekt utworzony i zarządzany przez kontener Springa. Tylko beany dostają wstrzyknięte zależności, wartości z `@Value` i zachowanie z adnotacji (`@Transactional`, `@Cacheable`, `@Async`...).

**Obiekt utworzony przez `new` nie jest beanem**, nawet jeśli jego klasa ma `@Component`. Spring o nim nie wie: pola z `@Value` i `@Autowired` zostają `null`/`0`, adnotacje nie działają.

### Jak zrobić beana

1. **Skanowanie komponentów**: adnotacja na klasie.

| Adnotacja | Zwyczajowo dla |
|---|---|
| `@Component` | ogólny komponent |
| `@Service` | logika biznesowa |
| `@Repository` | dostęp do danych (dodatkowo tłumaczy wyjątki bazy) |
| `@Controller` / `@RestController` | warstwa web |
| `@Configuration` | klasa z metodami `@Bean` |

2. **Metoda `@Bean`** w klasie `@Configuration`: gdy klasa nie jest Twoja (biblioteka) albo trzeba ją skonfigurować w kodzie.

```java
@Configuration
public class ClockConfig {
    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Europe/Warsaw"));
    }
}
```

### Co skanuje `@SpringBootApplication`

`@SpringBootApplication` = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`. Skanowany jest **pakiet klasy z tą adnotacją i jego podpakiety**. Klasa z `pl.kurs.review.ShopApplication` widzi `pl.kurs.review.order.*`, ale nie widzi `pl.kurs.invoices.*`. Rozwiązania: przenieść klasę, `scanBasePackages`, `@Import(Klasa.class)` albo metoda `@Bean`.

## Sposoby wstrzykiwania

```java
// 1. Konstruktor: ZALECANE. Pola final, zależności widać w sygnaturze, łatwy test bez Springa.
public OrderService(PaymentGateway gateway, Clock clock) { ... }
// Jeden konstruktor: @Autowired niepotrzebne.

// 2. Setter: dla zależności opcjonalnych.
@Autowired
void setNotifier(Notifier notifier) { ... }

// 3. Pole: krótko, ale pole nie może być final, a w teście jednostkowym trzeba refleksji.
@Autowired
private PaymentGateway gateway;
```

## Kilka beanów tego samego typu

Gdy są dwa beany `PaymentGateway`, wstrzyknięcie jednego kończy się:

```
NoUniqueBeanDefinitionException: No qualifying bean of type 'PaymentGateway' available:
expected single matching bean but found 2: blikGateway,cardGateway
```

Rozwiązania:

```java
@Component
@Primary                                    // domyślny, gdy nikt nie powie inaczej
public class CardGateway implements PaymentGateway { }

public RefundService(@Qualifier("blikGateway") PaymentGateway gateway) { }   // konkretny, po nazwie beana

public PaymentRouter(List<PaymentGateway> all) { }       // wszystkie naraz
public PaymentRouter(Map<String, PaymentGateway> all) { } // nazwa beana -> bean
```

Domyślna nazwa beana to nazwa klasy z małej litery (`CardGateway` -> `cardGateway`). `@Qualifier` wygrywa z `@Primary`.

Brak beana: `NoSuchBeanDefinitionException: No qualifying bean of type ... expected at least 1 bean`. Sprawdź: czy klasa ma adnotację, czy jest w skanowanym pakiecie, czy nie jest wyłączona warunkiem (`@Profile`, `@ConditionalOn...`).

## Zakres (scope) beana

| Scope | Ile instancji |
|---|---|
| `singleton` (domyślny) | **jedna na cały kontekst**, wspólna dla wszystkich wątków i żądań |
| `prototype` | nowa przy każdym pobraniu z kontenera |
| `request`, `session` | jedna na żądanie HTTP / sesję (aplikacje web) |

Wniosek: **singleton nie może trzymać stanu konkretnego żądania** w polach. Pole `List<String> lines` w serwisie jest wspólne dla wszystkich użytkowników i wątków naraz. Stan żądania trzymaj w zmiennych lokalnych i parametrach. W polach singletona mogą być: zależności (inne beany), stałe, konfiguracja, struktury bezpieczne wątkowo celowo współdzielone (np. cache).

`@Scope("prototype")` na serwisie wstrzykiwanym do singletona **nie pomaga**: singleton dostaje jedną instancję raz, przy swoim tworzeniu.

## Cykl życia beana (w skrócie)

1. utworzenie (konstruktor, wstrzyknięcie zależności przez konstruktor),
2. wstrzyknięcie pól i setterów, `@Value`,
3. `@PostConstruct` (inicjalizacja, gdy wszystko już jest wstrzyknięte),
4. opakowanie w **proxy**, jeśli bean ma np. `@Transactional` (szczegóły w rozdziale 09),
5. ... używanie ...
6. `@PreDestroy` przy zamykaniu kontekstu.

## Typowe błędy

- `new` na klasie, która jest beanem (`new DiscountPolicy()`): brak wstrzykiwania i adnotacji.
- `@Value` albo `@Autowired` na polu `static`: Spring je pomija (w logu: `Autowired annotation is not supported on static fields`).
- Klasa poza pakietem aplikacji.
- Stan żądania w polu singletona.
- Cykl zależności przez konstruktory (A potrzebuje B, B potrzebuje A): Spring Boot odmawia startu. Rozwiązanie: przeprojektować (trzecia klasa), nie „obchodzić”.

## Pytania sprawdzające

1. Co to jest bean? Czym różni się od obiektu utworzonego przez `new`?
2. Które pakiety skanuje `@SpringBootApplication`?
3. Dlaczego wstrzykiwanie przez konstruktor jest zalecane?
4. Co zrobić, gdy są dwa beany tego samego interfejsu? Podaj trzy sposoby.
5. Jaki jest domyślny scope beana i co z niego wynika dla pól klasy?
6. Kiedy użyć metody `@Bean` zamiast `@Component`?
