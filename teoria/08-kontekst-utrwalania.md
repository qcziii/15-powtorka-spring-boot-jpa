# 08. Kontekst utrwalania (persistence context)

Ćwiczenia: [15](../cwiczenia/15-encja-w-odpowiedzi-json/ZADANIE.md), [16](../cwiczenia/16-zmiany-same-sie-zapisuja/ZADANIE.md), [20](../cwiczenia/20-zmiany-klienta-gina/ZADANIE.md)

## Co to jest

Kontekst utrwalania (w Hibernate: `Session`, w JPA: `EntityManager`) to **pamięć podręczna encji wczytanych w danej transakcji**. W Springu domyślnie: jeden kontekst na transakcję.

- W jednym kontekście jeden wiersz to **jeden obiekt**: dwa `findById(1L)` zwracają ten sam obiekt, drugie bez zapytania do bazy (cache pierwszego poziomu).
- Kontekst pamięta stan każdej wczytanej encji i przy **flush** porównuje go z obecnym (**dirty checking**).

## Stany encji

```
 new Product(...)           persist / save              commit, clear, koniec transakcji
   TRANSIENT  ───────────────────────────>  MANAGED  ─────────────────────────────>  DETACHED
                              findById ───>    │  <───── merge / save(detached) ─────────┘
                                               │ remove
                                               v
                                            REMOVED  ── flush ──> delete
```

| Stan | Znaczenie |
|---|---|
| transient | zwykły obiekt, Hibernate o nim nie wie |
| managed | w kontekście: **zmiany pól trafią do bazy przy flush, bez `save`** |
| detached | był w kontekście, który się zamknął; zmiany nie trafią do bazy same |
| removed | oznaczony do usunięcia |

## Dirty checking

```java
@Transactional
public void rename(Long id, String name) {
    Product p = products.findById(id).orElseThrow();   // managed
    p.setName(name);                                    // przy commit: update product set name=? ...
}                                                       // save niepotrzebne
```

Konsekwencja w drugą stronę: **każda zmiana encji managed w transakcji zapisuje się**, także „tymczasowa, tylko do obliczeń”:

```java
@Transactional
public BigDecimal previewDiscount(Long id, int percent) {
    Product p = products.findById(id).orElseThrow();
    p.setPrice(p.getPrice().multiply(...));   // BŁĄD: to trafi do bazy
    return p.getPrice();
}
```

Licz wynik w zmiennej, nie zmieniaj encji. Encja to stan w bazie, nie notatnik.

## Flush

Flush = wysłanie zaległych `insert`/`update`/`delete` do bazy (to jeszcze nie commit). Hibernate robi flush:

- przed commitem transakcji,
- przed zapytaniem JPQL, którego wynik mógłby zależeć od niezapisanych zmian (tryb `AUTO`),
- przy jawnym `flush()` / `saveAndFlush()`.

W transakcji `@Transactional(readOnly = true)` Spring ustawia Hibernate tryb flush `MANUAL`: **zmiany encji i nowe encje (przy id z sekwencji) nie są zapisywane**, bez żadnego błędu. Przy `IDENTITY` insert idzie od razu przy `persist`, więc nowa encja się zapisze, a zmiany już nie: zachowanie zależy od strategii id. Dlatego metody zapisujące nie mogą być `readOnly`.

## save, persist, merge

`JpaRepository.save(entity)`:

- encja „nowa” według Spring Data (pole `@Id` jest `null`; przy id nadawanym ręcznie Spring Data uznaje encję za nową tylko z `@Version` albo `Persistable`): `persist`. Ten sam obiekt staje się managed i dostaje id,
- w przeciwnym razie: `merge`. Hibernate wczytuje wiersz (albo bierze z kontekstu) i **kopiuje do niego wszystkie pola** przekazanego obiektu, także `null`. Zwraca **inny obiekt** (managed); przekazany zostaje detached.

```java
// PATCH "zmień tylko nazwę" zrobiony przez nowy obiekt z id:
products.save(new Product(id, "Nowa nazwa", null, null));   // cena i stan nadpisane nullem!
```

Częściowa zmiana: wczytaj encję i zmień tylko podane pola (dirty checking zrobi resztę). Wynik `merge`/`save` zawsze przypisuj, jeśli dalej używasz obiektu: `product = products.save(product)`.

## Leniwe ładowanie i granice sesji

Leniwe relacje można doczytać tylko w otwartym kontekście. Po wyjściu z metody `@Transactional` encje są detached:

```java
@Transactional(readOnly = true)
public Author find(Long id) { return authors.findById(id).orElseThrow(); }

// w kontrolerze, po transakcji:
author.getBooks().size();     // LazyInitializationException
```

### Open Session In View

Spring Boot domyślnie ma `spring.jpa.open-in-view=true`: sesja jest otwarta przez całe żądanie HTTP, także w kontrolerze i podczas zamiany na JSON (Boot ostrzega o tym w logu przy starcie). Leniwe relacje „działają” wszędzie, ale:

- zapytania lecą z warstwy web, poza transakcją, często jako N+1 przy serializacji,
- połączenie z bazą jest zajęte do końca żądania.

Częsta praktyka: `open-in-view: false` i pobieranie wszystkiego, czego potrzebuje odpowiedź, w serwisie (DTO, `join fetch`). W tym repo wszędzie jest `false`.

## Typowe błędy

- Zmiana encji „tylko do obliczeń” w transakcji.
- `save(new Encja(id, ...))` jako częściowa aktualizacja.
- Ignorowanie wyniku `merge`/`save` dla obiektów detached.
- Metoda zapisująca w klasie `@Transactional(readOnly = true)` bez własnego `@Transactional`.
- Leniwa relacja używana poza transakcją; „naprawa” przez `EAGER` albo `open-in-view: true`.

## Pytania sprawdzające

1. Wymień stany encji. W jakim stanie jest encja zwrócona z `findById` wewnątrz transakcji? A po jej zakończeniu?
2. Co to jest dirty checking? Kiedy nie trzeba wołać `save`?
3. Kiedy Hibernate robi flush?
4. Czym różni się `persist` od `merge`? Co zwraca `merge`?
5. Co zmienia `readOnly = true` w zachowaniu Hibernate?
6. Co robi `spring.jpa.open-in-view` i dlaczego bywa wyłączane?
