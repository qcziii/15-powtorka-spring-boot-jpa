package pl.kurs.review;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ProductService {

    private final ProductRepository products;

    public ProductService(ProductRepository products) {
        this.products = products;
    }

    /** Creates a product from a CSV row "sku;name;price". Throws InvalidRowException for an invalid row. */
    @Transactional
    public Product createFromCsv(String row) {
        String[] parts = row.split(";");
        if (parts.length != 3 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new InvalidRowException("zły format: " + row);
        }
        BigDecimal price;
        try {
            price = new BigDecimal(parts[2].strip());
        } catch (NumberFormatException e) {
            throw new InvalidRowException("zła cena: " + parts[2]);
        }
        if (products.existsBySku(parts[0])) {
            throw new InvalidRowException("SKU już istnieje: " + parts[0]);
        }
        return products.save(new Product(parts[0], parts[1], price));
    }
}
