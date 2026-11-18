# 01. Maven

Ćwiczenia: [01](../cwiczenia/01-projekt-sie-nie-kompiluje/ZADANIE.md), [02](../cwiczenia/02-brakujacy-plik-i-zaleznosc/ZADANIE.md)

## Po co Maven

Maven robi trzy rzeczy:

1. **pobiera zależności** (biblioteki) z repozytorium (Maven Central) do `~/.m2/repository`,
2. **buduje** projekt według stałego cyklu: kompilacja, testy, paczka (`.jar`),
3. narzuca **konwencję katalogów**, dzięki której każdy projekt wygląda tak samo.

Wszystko opisuje plik `pom.xml` (Project Object Model).

## Współrzędne (GAV)

Każdy projekt i każda biblioteka ma trzy współrzędne:

```xml
<groupId>org.apache.commons</groupId>     <!-- organizacja, odwrócona domena -->
<artifactId>commons-lang3</artifactId>    <!-- nazwa modułu -->
<version>3.18.0</version>                 <!-- wersja; -SNAPSHOT = wersja w trakcie prac -->
```

## Konwencja katalogów

```
pom.xml
src/main/java          kod aplikacji            -> target/classes
src/main/resources     pliki na classpath        -> target/classes
src/test/java          testy                     -> target/test-classes
src/test/resources     pliki tylko dla testów    -> target/test-classes
target/                wynik budowania (nie commitujemy)
```

**Z `src/main/java` do `target/classes` trafiają tylko skompilowane klasy.** Plik `.properties`, `.yml`, `.sql` czy `.json` położony obok klas Javy nie znajdzie się na classpath. Pliki „nie-Java” idą do `src/main/resources` (w tej samej strukturze pakietów, jeśli kod szuka ich po ścieżce pakietu).

```java
// szuka pliku target/classes/pl/kurs/review/rates.properties
getClass().getResourceAsStream("/pl/kurs/review/rates.properties");
```

## Zależności i zakresy (scope)

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <!-- bez <version>: zarządza nią rodzic -->
    <!-- bez <scope>: compile -->
</dependency>
```

| Zakres | Kompilacja `src/main` | Testy | W gotowej aplikacji | Przykład |
|---|---|---|---|---|
| `compile` (domyślny) | tak | tak | tak | Spring, commons-lang3 |
| `runtime` | **nie** | tak | tak | sterownik bazy (H2, PostgreSQL) |
| `test` | **nie** | tak | nie | JUnit, AssertJ, Mockito |
| `provided` | tak | tak | nie (dostarcza serwer) | Servlet API na zewnętrznym Tomcacie, Lombok |

Biblioteka w zakresie `test` jest niewidoczna dla kodu w `src/main`: kompilator zgłosi `package ... does not exist`.

### Zależności przechodnie

Starter `spring-boot-starter-data-jpa` ciągnie Hibernate, Spring Data, pulę połączeń i kilkadziesiąt innych bibliotek. Drzewo zobaczysz poleceniem:

```bash
mvn dependency:tree
mvn dependency:tree -Dincludes=org.hibernate.orm     # tylko jedna gałąź
```

Gdy dwie ścieżki ciągną różne wersje tej samej biblioteki, Maven wybiera **najbliższą** w drzewie (nearest wins), a przy tej samej głębokości tę zadeklarowaną wcześniej. Stąd biorą się `NoSuchMethodError` i `ClassNotFoundException` w czasie działania. Rozwiązanie: jedna wersja ustalona w `<dependencyManagement>` (albo przez BOM).

## Rodzic, dependencyManagement, BOM

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.8</version>
</parent>
```

Rodzic Spring Boota daje:

- **wersje setek bibliotek** (w `<dependencyManagement>`): dlatego przy `commons-lang3`, `h2` czy starterach nie piszemy `<version>`. Wersja wpisana ręcznie nadpisuje tę z rodzica, co zwykle jest błędem,
- konfigurację wtyczek (kompilator, surefire, spring-boot-maven-plugin),
- właściwość `java.version`, z której bierze się `maven.compiler.release`: kompilator kompiluje dla tej wersji Javy **i sprawdza API tej wersji**. Przy `<java.version>17</java.version>` metoda `List.getLast()` (Java 21) „nie istnieje”, nawet gdy masz zainstalowany JDK 21.

`<dependencyManagement>` tylko **ustala wersje**, niczego nie dodaje do projektu. `<dependencies>` dodaje.

BOM (Bill of Materials) to pom z samym `dependencyManagement`, importowany tak:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2025.1.3</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Efektywny pom (po złożeniu rodziców i BOM-ów): `mvn help:effective-pom`.

## Startery Spring Boota

Starter to pusta paczka z zestawem zależności pod jedno zastosowanie:

| Starter | Co daje |
|---|---|
| `spring-boot-starter` | rdzeń Springa, autokonfiguracja, logowanie, YAML |
| `spring-boot-starter-webmvc` | Spring MVC, Tomcat, Jackson (JSON) |
| `spring-boot-starter-validation` | Bean Validation: API **i** implementacja (Hibernate Validator) |
| `spring-boot-starter-data-jpa` | Spring Data JPA, Hibernate, HikariCP |
| `spring-boot-starter-test` | JUnit, AssertJ, Mockito, Spring Test, JsonPath |

API (np. `jakarta.validation-api`) to same interfejsy i adnotacje: kod się kompiluje, ale w czasie działania nie ma kto walidować (`NoProviderFoundException`). Starter daje oba.

## Cykl życia (lifecycle)

```
validate -> compile -> test-compile -> test -> package -> verify -> install -> deploy
```

Uruchomienie fazy uruchamia **wszystkie wcześniejsze**. `mvn package` kompiluje, testuje i pakuje. `clean` to osobny cykl (usuwa `target/`).

| Polecenie | Co robi |
|---|---|
| `mvn compile` | kompiluje `src/main` |
| `mvn test` | + kompiluje i uruchamia testy |
| `mvn package` | + buduje `target/*.jar` |
| `mvn install` | + kopiuje jar do `~/.m2/repository` (inne projekty na komputerze go widzą) |
| `mvn clean package -DskipTests` | od zera, bez uruchamiania testów |
| `mvn spring-boot:run` | uruchamia aplikację Boot |
| `mvn -pl cwiczenia/03-ktory-bean test` | jeden moduł z projektu wielomodułowego |

Wtyczki robią właściwą robotę: `maven-compiler-plugin` (compile), `maven-surefire-plugin` (test), `spring-boot-maven-plugin` (wykonywalny jar).

## Projekt wielomodułowy

To repo jest takim projektem: główny `pom.xml` ma `<packaging>pom</packaging>` i listę `<modules>`, a każde ćwiczenie ma swój `pom.xml` z `<parent>` wskazującym na główny. Rodzic przekazuje wersje i konfigurację, agregator pozwala zbudować wszystko jednym poleceniem.

## Maven Wrapper

`mvnw` / `mvnw.cmd` w projekcie pobiera właściwą wersję Mavena, więc każdy buduje tym samym. Używa się go jak `mvn`: `./mvnw test`.

## Typowe błędy

- Biblioteka używana w `src/main` ma `<scope>test</scope>` (bo „skopiowałem z testów”).
- Ręcznie wpisana `<version>` biblioteki zarządzanej przez Boota: niezgodne wersje Springa i Hibernate.
- Plik konfiguracyjny w `src/main/java` zamiast w `src/main/resources`.
- Sterownik bazy w `compile` zamiast `runtime` (działa, ale kod może przypadkiem zależeć od klas sterownika).
- Za niska `java.version` względem używanego API.
- Samo API (`jakarta.validation-api`, `jakarta.persistence-api`) zamiast startera z implementacją.

## Pytania sprawdzające

1. Co oznaczają `groupId`, `artifactId`, `version`? Co to jest wersja `-SNAPSHOT`?
2. Jaki zakres ma sterownik JDBC i dlaczego?
3. Co się stanie z plikiem `config.json` położonym w `src/main/java/pl/kurs/`?
4. Czym się różni `<dependencyManagement>` od `<dependencies>`?
5. Co uruchomi `mvn package`? A `mvn install`?
6. Jak sprawdzić, skąd w projekcie wzięła się dana biblioteka?
7. Dlaczego przy starterach Spring Boota nie podajemy wersji?
