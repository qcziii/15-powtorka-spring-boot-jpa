# 13. Komentarze nie są powiązane z postem w bazie

Poziom: **średni (2 błędy)** · Rodzaj: **napraw** · Teoria: [07. Relacje między encjami](../../teoria/07-relacje.md)

## Uruchomienie

```bash
./cwiczenia/13-komentarze-bez-posta/check.sh
```

## Zgłoszenie

Post ma listę komentarzy (`@OneToMany`), komentarz ma swój post (`@ManyToOne`). Komentarze się zapisują, ale:

- kolumna `post_id` w tabeli `comment` jest pusta,
- `CommentRepository.findByPostId` nic nie znajduje,
- w bazie jest trzecia tabela, `post_comments`, której nikt nie zamawiał.

**Oczekiwane:** relacja jest zapisana tylko w kolumnie `comment.post_id`, w bazie są tylko tabele `post` i `comment`.

## Wymagania

- Nie zmieniaj `BlogService` ani testu.

## Wskazówki

- Ile jest tu relacji z punktu widzenia Hibernate: jedna dwukierunkowa czy dwie jednokierunkowe? Co mówi o tym `mappedBy`?
- Która strona relacji jest **właścicielem** (decyduje o kolumnie `post_id`)? Czy kod ją ustawia?

## Pytania dodatkowe

1. Co oznacza `mappedBy = "post"`? Do czego odnosi się `"post"`?
2. Co się stanie, gdy ustawisz tylko `post.getComments().add(c)`, a nie `c.setPost(post)`? A odwrotnie?
3. Po co metoda pomocnicza `addComment`, która ustawia obie strony?
4. Skąd Hibernate wziął nazwę tabeli `post_comments`?

## Kiedy skończysz

`check.sh` ma wypisać `WYNIK: wszystkie sprawdzenia przeszły`. Potem odpowiedz na pytania dodatkowe.
