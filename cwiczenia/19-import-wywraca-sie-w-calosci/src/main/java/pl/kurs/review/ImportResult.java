package pl.kurs.review;

import java.util.List;

/** Result of the import: how many products were saved and the errors of the rejected rows. */
public record ImportResult(int saved, List<String> errors) {
}
