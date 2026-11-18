package pl.kurs.review;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ProductService {

    private final ProductRepository products;

    public ProductService(ProductRepository products) {
        this.products = products;
    }

    /**
     * Shows what the price WOULD be after a discount (for the "simulate a promotion" screen).
     * Must not change anything in the database.
     */
    @Transactional
    public BigDecimal previewDiscount(Long id, int percent) {
        Product product = products.findById(id).orElseThrow();
        BigDecimal factor = BigDecimal.valueOf(100 - percent).movePointLeft(2);
        product.setPrice(product.getPrice().multiply(factor).setScale(2, RoundingMode.HALF_UP));
        return product.getPrice();
    }

    /** Changes only the fields given in the change (null = do not change). */
    @Transactional
    public void change(Long id, ProductChange change) {
        if (!products.existsById(id)) {
            throw new IllegalArgumentException("Nie ma produktu " + id);
        }
        products.save(new Product(id, change.name(), change.price(), change.stock()));
    }
}
