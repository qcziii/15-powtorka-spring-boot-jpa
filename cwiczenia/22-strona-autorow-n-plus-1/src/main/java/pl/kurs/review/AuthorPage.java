package pl.kurs.review;

import java.util.List;

/** One page of the authors list: the authors of the page (with sorted titles) and the totals. */
public record AuthorPage(List<AuthorRow> authors, long totalAuthors, int totalPages) {

    public record AuthorRow(String name, List<String> titles) {
    }
}
