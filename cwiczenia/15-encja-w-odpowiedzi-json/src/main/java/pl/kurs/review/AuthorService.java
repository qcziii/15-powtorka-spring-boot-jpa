package pl.kurs.review;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthorService {

    private final AuthorRepository authors;

    public AuthorService(AuthorRepository authors) {
        this.authors = authors;
    }

    @Transactional(readOnly = true)
    public Optional<Author> find(Long id) {
        return authors.findById(id);
    }
}
