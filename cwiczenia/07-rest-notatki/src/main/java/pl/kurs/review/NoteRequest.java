package pl.kurs.review;

/** Request body of POST and PUT: {"title":"...","content":"..."}. */
public record NoteRequest(String title, String content) {
}
