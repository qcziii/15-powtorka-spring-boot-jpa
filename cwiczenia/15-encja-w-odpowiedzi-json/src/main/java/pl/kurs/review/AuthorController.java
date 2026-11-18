package pl.kurs.review;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorController {

    private final AuthorService service;

    public AuthorController(AuthorService service) {
        this.service = service;
    }

    /** GET /api/authors/1 -> {"id":1,"name":"...","books":[{"id":1,"title":"...","publishedYear":1961}]} */
    @GetMapping("/api/authors/{id}")
    public ResponseEntity<Author> get(@PathVariable Long id) {
        return ResponseEntity.of(service.find(id));
    }
}
