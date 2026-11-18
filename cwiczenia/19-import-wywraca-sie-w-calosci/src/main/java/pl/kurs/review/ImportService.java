package pl.kurs.review;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImportService {

    private final ProductService products;

    public ImportService(ProductService products) {
        this.products = products;
    }

    /**
     * Imports the price list. Valid rows are saved, invalid ones are skipped and reported
     * ("wiersz 2: zła cena: abc"). One invalid row must not stop the import.
     */
    @Transactional
    public ImportResult importCsv(List<String> rows) {
        int saved = 0;
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            try {
                products.createFromCsv(rows.get(i));
                saved++;
            } catch (InvalidRowException e) {
                errors.add("wiersz " + (i + 1) + ": " + e.getMessage());
            }
        }
        return new ImportResult(saved, errors);
    }
}
