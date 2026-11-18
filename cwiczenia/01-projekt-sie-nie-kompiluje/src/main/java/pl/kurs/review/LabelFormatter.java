package pl.kurs.review;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Builds shelf labels for products. Do not change this class: the problem is in pom.xml.
 */
public class LabelFormatter {

    /** "kawa ziarnista" -> "Kawa ziarnista", null/blank -> "?". */
    public String productName(String raw) {
        if (StringUtils.isBlank(raw)) {
            return "?";
        }
        return StringUtils.capitalize(raw.strip());
    }

    /** Label of the newest price: the last element of the price history. */
    public String currentPrice(List<String> priceHistory) {
        return priceHistory.getLast() + " zł";
    }

    /** "Kawa ziarnista | 39.99 zł" */
    public String label(String rawName, List<String> priceHistory) {
        return productName(rawName) + " | " + currentPrice(priceHistory);
    }
}
