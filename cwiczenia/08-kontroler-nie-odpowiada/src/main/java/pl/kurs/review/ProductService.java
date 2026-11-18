package pl.kurs.review;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** Products kept in memory. This class is correct: do not change it. */
@Service
public class ProductService {

    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong();

    public Product create(ProductRequest request) {
        if (request.name() == null || request.price() == null) {
            throw new IllegalArgumentException("Produkt bez nazwy albo ceny: " + request);
        }
        long id = ids.incrementAndGet();
        Product product = new Product(id, request.name(), request.category(), request.price());
        products.put(id, product);
        return product;
    }

    public Optional<Product> findById(long id) {
        return Optional.ofNullable(products.get(id));
    }

    /** Products of the category (null = all), sorted by id. */
    public List<Product> find(String category) {
        return products.values().stream()
                .filter(p -> category == null || category.equalsIgnoreCase(p.category()))
                .sorted(Comparator.comparingLong(Product::id))
                .toList();
    }

    public void deleteAll() {
        products.clear();
    }
}
