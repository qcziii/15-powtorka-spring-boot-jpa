package pl.kurs.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorListService {

    static final int PAGE_SIZE = 5;

    private final AuthorRepository authors;

    public AuthorListService(AuthorRepository authors) {
        this.authors = authors;
    }

    /** Page of authors sorted by name (page from 0), each with the sorted titles of their books. */
    @Transactional(readOnly = true)
    public AuthorPage page(int page) {
        Page<Author> result = authors.findAll(PageRequest.of(page, PAGE_SIZE, Sort.by("name")));
        List<AuthorPage.AuthorRow> rows = result.getContent().stream()
                .map(a -> new AuthorPage.AuthorRow(a.getName(), a.getBooks().stream().map(Book::getTitle).sorted().toList()))
                .toList();
        return new AuthorPage(rows, result.getTotalElements(), result.getTotalPages());
    }
}
