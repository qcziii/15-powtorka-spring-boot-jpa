# 03. Spring Boot i konfiguracja

Ćwiczenia: [04](../cwiczenia/04-konfiguracja-ignorowana/ZADANIE.md), [06](../cwiczenia/06-ustawienia-i-profile/ZADANIE.md)

## Co dodaje Spring Boot do Springa

Spring to kontener i moduły (MVC, Data, Security...). Spring Boot dodaje:

1. **startery**: zestawy zależności (rozdział 01),
2. **autokonfigurację**: beany tworzone automatycznie, gdy na classpath jest odpowiednia biblioteka, a Ty nie zdefiniowałeś własnego (`@ConditionalOnClass`, `@ConditionalOnMissingBean`). Jest H2 i Spring Data JPA, więc Boot tworzy `DataSource`, `EntityManagerFactory`, `TransactionManager`. Jest Jackson, więc `JsonMapper`,
3. **zewnętrzną konfigurację**: `application.yml`/`application.properties`, zmienne środowiskowe, argumenty, profile,
4. **wbudowany serwer** (Tomcat) i wykonywalny jar: `java -jar app.jar`.

```java
@SpringBootApplication
public class ShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }
}
```

Co Boot skonfigurował i dlaczego: uruchom z `--debug` (raport `CONDITIONS EVALUATION REPORT`). Własny bean danego typu zwykle wyłącza autokonfigurację tego typu.

## application.yml

```yaml
server:
  port: 8081
spring:
  datasource:
    url: jdbc:h2:mem:shop
shop:
  name: Sklep Kursanta
  countries:          # lista
    - PL
    - CZ
  shipping:
    price: 15.00
    free-from: 200.00
```

To samo w `application.properties`:

```properties
shop.shipping.free-from=200.00
shop.countries[0]=PL
```

**W YAML-u wcięcie (spacje, nie tabulatory) decyduje o kluczu.** `shipping:` bez wcięcia to klucz `shipping.price`, a nie `shop.shipping.price`. Spring nie zgłosi błędu: po prostu `shop.shipping` będzie puste.

## @Value

```java
@Value("${shop.discount.percent}")          // brak klucza = błąd przy starcie
private int percent;

@Value("${shop.discount.percent:0}")        // wartość domyślna po dwukropku
private int percent;
```

Działa tylko w beanie, na polach instancji, parametrach konstruktora i setterach. Nie działa na polach `static` ani w obiektach z `new`.

## @ConfigurationProperties

Dla grupy ustawień lepszy jest jeden obiekt z typami:

```java
@ConfigurationProperties(prefix = "shop")
public record ShopProperties(String name, Shipping shipping, List<String> countries) {
    public record Shipping(BigDecimal price, BigDecimal freeFrom) { }
}
```

Sama adnotacja **nie rejestruje** beana. Trzeba jednego z:

```java
@SpringBootApplication
@ConfigurationPropertiesScan                         // wszystkie z pakietu aplikacji
public class ShopApplication { }

@EnableConfigurationProperties(ShopProperties.class) // konkretne klasy
```

**Relaxed binding**: `free-from`, `freeFrom`, `free_from` i zmienna środowiskowa `SHOP_SHIPPING_FREEFROM` trafiają w to samo pole. W plikach zalecany jest zapis kebab-case (`free-from`).

Walidacja ustawień przy starcie: `@Validated` na klasie i adnotacje (`@NotBlank`, `@Positive`) na polach (wymaga startera validation).

| | `@Value` | `@ConfigurationProperties` |
|---|---|---|
| pojedyncza wartość | wygodne | za dużo kodu |
| grupa ustawień | rozsypane po klasach | jeden obiekt z typami |
| relaxed binding | ograniczony | pełny |
| walidacja | nie | `@Validated` |
| podpowiedzi w IDE | nie | tak (z `spring-boot-configuration-processor`) |

## Profile

Profil to nazwany zestaw konfiguracji: `dev`, `test`, `prod`.

- `application.yml`: zawsze,
- `application-dev.yml`: **dodatkowo** w profilu `dev`. Nazwa musi mieć postać `application-{profil}.yml` (myślnik!). `application.dev.yml` czy `application_dev.yml` są ignorowane.

Pliki się **łączą**: klucze z profilu nadpisują te same klucze z `application.yml`, reszta zostaje.

Włączanie profilu:

```bash
java -jar app.jar --spring.profiles.active=dev
SPRING_PROFILES_ACTIVE=dev java -jar app.jar
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

W teście: `@ActiveProfiles("dev")`. Bean tylko w profilu: `@Profile("dev")` na klasie albo metodzie `@Bean`.

## Kolejność źródeł konfiguracji (od najsilniejszego, w skrócie)

1. właściwości z testu (`@SpringBootTest(properties = ...)`, `@TestPropertySource`),
2. argumenty wiersza poleceń (`--server.port=9000`),
3. zmienne środowiskowe (`SERVER_PORT=9000`),
4. `application-{profil}.yml`,
5. `application.yml`,
6. wartości domyślne (`${klucz:domyślna}`, domyślne Boota).

Dzięki temu ten sam jar działa na każdym środowisku: hasło do bazy przychodzi ze zmiennej środowiskowej, nie z pliku w repo.

## Typowe błędy

- Złe wcięcie w YAML-u, tabulator zamiast spacji.
- Plik profilu z kropką albo podkreśleniem zamiast myślnika.
- `@ConfigurationProperties` bez `@ConfigurationPropertiesScan`/`@EnableConfigurationProperties`.
- Hasła i klucze w `application.yml` w repozytorium.
- Literówka w kluczu: `@ConfigurationProperties` po cichu zostawia `null`, `@Value` bez domyślnej wywraca start (lepiej!).

## Pytania sprawdzające

1. Co to jest autokonfiguracja? Jak sprawdzić, co Boot skonfigurował?
2. Jak Boot łączy `application.yml` i `application-dev.yml`?
3. Jak nadpisać `server.port` bez zmiany plików?
4. Co jest potrzebne, żeby rekord z `@ConfigurationProperties` stał się beanem?
5. Co to jest relaxed binding?
6. Jak w teście włączyć profil, a jak nadpisać pojedynczą właściwość?
