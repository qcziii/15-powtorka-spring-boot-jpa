# 07. Relacje między encjami

Ćwiczenia: [13](../cwiczenia/13-komentarze-bez-posta/ZADANIE.md), [14](../cwiczenia/14-pozycje-zamowienia/ZADANIE.md), [15](../cwiczenia/15-encja-w-odpowiedzi-json/ZADANIE.md)

## Rodzaje relacji

| Adnotacja | Przykład | W bazie |
|---|---|---|
| `@ManyToOne` | wiele komentarzy -> jeden post | kolumna `post_id` w tabeli `comment` |
| `@OneToMany` | post -> lista komentarzy | (ta sama kolumna `post_id`, widziana z drugiej strony) |
| `@OneToOne` | użytkownik -> profil | kolumna `profile_id` (unikalna) |
| `@ManyToMany` | student <-> kurs | tabela łącząca `student_course` |

W bazie relacja jeden-do-wielu to **jedna kolumna klucza obcego po stronie „wielu”**. W Javie możesz ją widzieć z jednej strony albo z obu.

## Relacja dwukierunkowa i strona właściciela

```java
@Entity
public class Post {
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public void addComment(Comment c) {     // metoda pomocnicza: ustawia OBIE strony
        comments.add(c);
        c.setPost(this);
    }

    public void removeComment(Comment c) {
        comments.remove(c);
        c.setPost(null);
    }
}

@Entity
public class Comment {
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;                      // STRONA WŁAŚCICIELA: to pole decyduje o post_id
}
```

- **Właściciel** relacji to strona, której pole Hibernate zapisuje do kolumny. Przy `@OneToMany`/`@ManyToOne` właścicielem jest zawsze `@ManyToOne` (tam jest klucz obcy).
- `mappedBy = "post"` na `@OneToMany` mówi: „ta lista to odbicie pola `post` w `Comment`; nie zapisuj jej osobno”.
- **Bez `mappedBy`** Hibernate traktuje `@OneToMany` jako osobną, jednokierunkową relację i tworzy **tabelę łączącą** `post_comments`. Masz wtedy dwie niezależne relacje: listę w tabeli łączącej i `post_id`, które nikt nie ustawia.
- Hibernate patrzy tylko na stronę właściciela. `post.getComments().add(c)` bez `c.setPost(post)` nie zapisze `post_id`. Dlatego metody pomocnicze ustawiają obie strony.

## Kaskady

Kaskada przenosi operację z rodzica na dzieci:

```java
@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
private List<OrderLine> lines = new ArrayList<>();
```

| Kaskada | Operacja na rodzicu -> na dzieciach |
|---|---|
| `PERSIST` | zapis nowego rodzica zapisuje nowe dzieci |
| `MERGE` | `merge` rodzica robi `merge` dzieci |
| `REMOVE` | usunięcie rodzica usuwa dzieci |
| `ALL` | wszystkie powyższe (i `REFRESH`, `DETACH`) |

Bez kaskady `save(order)` z nowymi pozycjami: pozycje nie trafiają do bazy (po stronie `mappedBy` po cichu; gdy zapisywana encja wskazuje polem `@ManyToOne` na niezapisaną: `TransientPropertyValueException`), a usunięcie zamówienia z pozycjami w bazie kończy się błędem klucza obcego.

**`orphanRemoval = true`**: dziecko **wyjęte z kolekcji** (sierota) jest usuwane z bazy. `CascadeType.REMOVE` działa tylko przy usuwaniu rodzica, nie przy wyjęciu z listy.

Kaskady stosuj dla **części całości** (pozycje zamówienia, komentarze posta), czyli dzieci, które nie istnieją bez rodzica. **Nigdy** `CascadeType.REMOVE`/`ALL` na `@ManyToOne` do czegoś współdzielonego (produkt z katalogu, kategoria): usunięcie pozycji usunęłoby produkt.

## Fetch: LAZY i EAGER

| Relacja | Domyślnie |
|---|---|
| `@ManyToOne`, `@OneToOne` | `EAGER` |
| `@OneToMany`, `@ManyToMany` | `LAZY` |

- `LAZY`: zamiast danych jest proxy/kolekcja-zaślepka; zapytanie leci przy pierwszym użyciu. Wymaga **otwartej sesji** (transakcji), inaczej `LazyInitializationException: could not initialize proxy - no Session` / `Cannot lazily initialize collection ... (no session)`.
- `EAGER`: dane zawsze są pobierane, nawet gdy nikt ich nie potrzebuje, i **nie chroni przed N+1** (rozdział 10).

Zalecenie: **wszystkie relacje `LAZY`** (`@ManyToOne(fetch = FetchType.LAZY)`), a to, czego potrzebujesz w danym przypadku, pobieraj jawnie (`join fetch`, `@EntityGraph`).

## Encje w JSON-ie

Zwrócenie encji z relacją dwukierunkową z kontrolera:

- `Author.books` -> `Book.author` -> `Author.books` -> ...: nieskończona rekurencja. Jackson 3 przerywa ją błędem `Document nesting depth (501) exceeds the maximum allowed (500)`, a odpowiedź może być już częściowo wysłana ze statusem 200,
- leniwe relacje serializowane po zamknięciu sesji: `LazyInitializationException`,
- wszystkie pola encji trafiają do klienta (też wewnętrzne),
- każda zmiana encji zmienia API.

Rozwiązanie: **DTO** (rekordy) budowane w serwisie, w transakcji:

```java
public record AuthorDto(Long id, String name, List<BookDto> books) {
    public record BookDto(Long id, String title, int publishedYear) { }
}
```

`@JsonIgnore`/`@JsonManagedReference` na encjach to łatanie objawów: miesza warstwę bazy z warstwą API.

## equals i hashCode w encjach (w skrócie)

Nie generuj `equals`/`hashCode` ze wszystkich pól (relacje leniwe, zmienne pola). Bezpieczny wzór: porównanie po `id`, gdy jest ustawione, i stały `hashCode` dla klasy:

```java
@Override
public boolean equals(Object o) {
    return o instanceof Book other && id != null && id.equals(other.id);
}

@Override
public int hashCode() {
    return Book.class.hashCode();
}
```

## Typowe błędy

- `@OneToMany` bez `mappedBy`: tabela łącząca i pusty klucz obcy.
- Ustawianie tylko strony odwrotnej (listy).
- Brak kaskady dla dzieci tworzonych razem z rodzicem; brak `orphanRemoval` przy usuwaniu z listy.
- `CascadeType.ALL` na `@ManyToOne`.
- `EAGER` „żeby nie było LazyInitializationException”.
- Encje w odpowiedziach REST.

## Pytania sprawdzające

1. Która strona relacji `Post`-`Comment` jest właścicielem i dlaczego?
2. Co robi `mappedBy`? Co się stanie bez niego?
3. Czym się różni `CascadeType.REMOVE` od `orphanRemoval`?
4. Jakie są domyślne typy pobierania dla `@ManyToOne` i `@OneToMany`?
5. Kiedy leci `LazyInitializationException`?
6. Dlaczego nie zwracać encji z relacją dwukierunkową z kontrolera?
