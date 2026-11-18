# 09. Transakcje

Ćwiczenia: [17](../cwiczenia/17-przelew-gubi-pieniadze/ZADANIE.md), [18](../cwiczenia/18-audyt-i-anulowanie/ZADANIE.md), [19](../cwiczenia/19-import-wywraca-sie-w-calosci/ZADANIE.md), [20](../cwiczenia/20-zmiany-klienta-gina/ZADANIE.md)

## Po co

Transakcja to grupa operacji na bazie, która wykonuje się **w całości albo wcale** (ACID: atomowość, spójność, izolacja, trwałość). Przelew = odjęcie z konta A + dodanie do konta B. Bez transakcji błąd w połowie zostawia pieniądze „w powietrzu”.

**Bez `@Transactional` każde wywołanie repozytorium to osobna transakcja.** `save` w linii 3 jest zatwierdzony (commit), zanim linia 5 rzuci wyjątek.

## @Transactional

```java
@Service
public class TransferService {

    @Transactional
    public void transfer(String from, String to, BigDecimal amount) {
        Account a = accounts.findById(from).orElseThrow();
        Account b = accounts.findById(to).orElseThrow(() -> new AccountNotFoundException(to));
        a.withdraw(amount);         // dirty checking: save niepotrzebne
        b.deposit(amount);
    }                               // koniec metody: commit; wyjątek: rollback
}
```

Zasady:

- transakcję zaczyna się w **serwisie**, na metodzie realizującej cały przypadek użycia (nie w kontrolerze, nie w każdej metodzie repozytorium osobno),
- `@Transactional` na klasie = na każdej publicznej metodzie; adnotacja na metodzie nadpisuje tę z klasy,
- używaj `org.springframework.transaction.annotation.Transactional` (ma `readOnly`, `rollbackFor`, `propagation`).

## Kiedy rollback

**Domyślnie Spring wycofuje transakcję tylko przy `RuntimeException` i `Error`.** Wyjątek kontrolowany (`extends Exception`) wylatujący z metody → **commit**.

```java
@Transactional(rollbackFor = SuspiciousTransferException.class)   // także ten wyjątek kontrolowany
@Transactional(rollbackFor = Exception.class)                     // każdy wyjątek
@Transactional(noRollbackFor = InvalidRowException.class)         // ten RuntimeException NIE wycofuje
```

Wyjątek złapany w `try/catch` wewnątrz metody i niewyrzucony dalej nie powoduje rollbacku (proxy go nie widzi).

## Jak to działa: proxy

Spring nie zmienia Twojej klasy. Tworzy **proxy**: obiekt-pośrednika, który jest wstrzykiwany innym beanom zamiast oryginału.

```
kontroler ──> [proxy TransferService] ──> TransferService.transfer()
               1. begin
               2. wywołaj metodę
               3. commit albo rollback
```

Wniosek: **transakcja działa tylko wtedy, gdy wywołanie przechodzi przez proxy**. Nie przechodzi, gdy:

- **self-invocation**: metoda wywołuje inną metodę **tej samej klasy** (`this.doCancel()`). Adnotacja na `doCancel` jest ignorowana. Liczy się adnotacja metody wywołanej z zewnątrz,
- metoda jest `private` (proxy jej nie przechwyci) albo `final`,
- obiekt powstał przez `new`, nie jest beanem.

Rozwiązania self-invocation: przenieść adnotację na metodę wywoływaną z zewnątrz; wydzielić metodę do **osobnego beana** (najczytelniejsze, np. `AuditService`); w ostateczności wstrzyknąć proxy samego siebie albo użyć `TransactionTemplate`.

## Propagacja

Co zrobić, gdy metoda transakcyjna woła inną metodę transakcyjną (przez proxy)?

| Propagacja | Gdy transakcja już jest | Gdy jej nie ma |
|---|---|---|
| `REQUIRED` (domyślna) | **dołącz** do niej | utwórz nową |
| `REQUIRES_NEW` | **zawieś** ją i utwórz nową, niezależną | utwórz nową |
| `SUPPORTS` | dołącz | działaj bez transakcji |
| `MANDATORY` | dołącz | wyjątek |
| `NEVER` | wyjątek | bez transakcji |
| `NESTED` | savepoint w bieżącej (JDBC) | utwórz nową |

`REQUIRES_NEW` dla rzeczy, które mają przetrwać rollback głównej operacji: audyt, log prób, licznik błędów. Wewnętrzna transakcja ma własny commit. Uwaga: zajmuje drugie połączenie z puli.

## Rollback-only i UnexpectedRollbackException

Przy `REQUIRED` metoda wewnętrzna **dołącza** do zewnętrznej transakcji. Gdy z wewnętrznej metody wylatuje `RuntimeException` przez proxy, Spring **oznacza całą transakcję jako rollback-only** (nie może jej wycofać „w połowie”). Jeśli metoda zewnętrzna złapie wyjątek i będzie kontynuować, przy commicie dostanie:

```
UnexpectedRollbackException: Transaction silently rolled back because it has been marked as rollback-only
```

i **wszystko** zostaje wycofane. Typowy przypadek: import w pętli z `try/catch` wokół wywołania transakcyjnego serwisu.

Rozwiązania (każde zmienia zachowanie, wybierz świadomie):

- sprawdzać poprawność **przed** wywołaniem (walidacja bez wyjątku z metody transakcyjnej),
- `noRollbackFor` dla wyjątku biznesowego rzucanego, zanim cokolwiek zapisano,
- `REQUIRES_NEW` dla każdego wiersza (każdy wiersz osobno zatwierdzany; zewnętrzna transakcja traci sens),
- usunąć `@Transactional` z metody wewnętrznej, jeśli i tak zawsze jest wołana w transakcji.

## readOnly

```java
@Transactional(readOnly = true)
public List<ProductDto> list() { ... }
```

Daje: wskazówkę dla bazy/sterownika, flush `MANUAL` w Hibernate (brak dirty checkingu przy commit = szybciej), czasem routing do repliki. Konsekwencja: **zmiany w takiej transakcji są gubione bez błędu** (patrz rozdział 08). Wzorzec „klasa `readOnly = true`, metody zapisujące z własnym `@Transactional`” jest popularny, ale łatwo zapomnieć o adnotacji na nowej metodzie.

## Izolacja (w skrócie)

Problemy przy równoległych transakcjach: brudny odczyt, niepowtarzalny odczyt, fantomy, **utracona aktualizacja** (dwie transakcje czytają saldo 100, obie odejmują 30, obie zapisują 70). `@Transactional(isolation = ...)` ustawia poziom izolacji bazy. Na utraconą aktualizację w JPA zwykle stosuje się:

- **optimistic locking**: pole `@Version` w encji; konflikt = `OptimisticLockException` (spróbuj ponownie),
- **pessimistic locking**: `@Lock(LockModeType.PESSIMISTIC_WRITE)` na metodzie repozytorium (`select ... for update`).

## Typowe błędy

- Brak `@Transactional` przy operacji z kilkoma zapisami.
- Wyjątek kontrolowany bez `rollbackFor`.
- `@Transactional` na metodzie wywoływanej przez `this` albo na metodzie `private`.
- `try/catch` wokół metody transakcyjnej (`REQUIRED`) i kontynuowanie: `UnexpectedRollbackException`.
- `readOnly = true` na metodzie, która zapisuje.
- Długie transakcje (np. wywołanie zewnętrznego API w środku): połączenie z bazą zajęte, blokady trzymane.

## Pytania sprawdzające

1. Ile transakcji jest w metodzie serwisu bez `@Transactional`, która woła dwa razy `save`?
2. Przy jakich wyjątkach Spring domyślnie robi rollback? Jak to zmienić?
3. Dlaczego `@Transactional` nie działa przy wywołaniu przez `this`?
4. Czym różni się `REQUIRED` od `REQUIRES_NEW`? Podaj przykład użycia `REQUIRES_NEW`.
5. Skąd bierze się `UnexpectedRollbackException`?
6. Co daje i czym grozi `readOnly = true`?
7. Jak zapobiec utraconej aktualizacji przy dwóch równoczesnych zmianach salda?
