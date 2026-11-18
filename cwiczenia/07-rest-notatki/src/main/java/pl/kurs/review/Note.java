package pl.kurs.review;

/** A note. Returned as JSON: {"id":1,"title":"...","content":"..."}. */
public record Note(long id, String title, String content) {
}
