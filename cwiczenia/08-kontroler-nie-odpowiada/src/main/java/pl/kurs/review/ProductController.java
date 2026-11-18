package pl.kurs.review;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    /** GET /api/products?category=kawa (category is optional: without it, all products). */
    @GetMapping
    public List<Product> list(@RequestParam String category) {
        return service.find(category);
    }

    /** GET /api/products/5 */
    @GetMapping("/{id}")
    public ResponseEntity<Product> get(@RequestParam long id) {
        return ResponseEntity.of(service.findById(id));
    }

    /** POST /api/products with JSON {"name":"...","category":"...","price":12.50} */
    @PostMapping
    public ResponseEntity<Product> create(ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
}
